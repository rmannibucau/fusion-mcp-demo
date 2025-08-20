package com.github.rmannibucau.javaccino.fusion.mcp.demo.mcp.protocol;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

// todo: enhance 1. the api 2. the thread safety and reactivity
public class SseBus implements Flow.Publisher<ByteBuffer> {
    private final Lock lock = new ReentrantLock();
    private final AtomicLong pending = new AtomicLong();
    private final Deque<String> messages = new ConcurrentLinkedDeque<>();
    private Flow.Subscriber<? super ByteBuffer> sse;

    public void publish(final String line) {
        messages.add(line);
    }

    public void cancel() {
        if (sse != null) {
            sse.onComplete();
        }
    }

    @Override
    public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
        lock.lock();
        try {
            if (sse != null) {
                try {
                    sse.onComplete();
                } catch (final RuntimeException re) {
                    Logger.getLogger(getClass().getName()).log(SEVERE, re, re::getMessage);
                }
            }
            sse = subscriber;
        } finally {
            lock.unlock();
        }
        subscriber.onSubscribe(new Flow.Subscription() {
            @Override
            public void request(final long n) {
                try {
                    pending.addAndGet(n);
                    while (!messages.isEmpty() && pending.updateAndGet(p -> {
                        if (p == Long.MAX_VALUE) {
                            return 1;
                        }
                        if (p > 0) {
                            return p - 1;
                        }
                        return -1;
                    }) >= 0) {
                        final var m = messages.pollFirst();
                        if (m != null) { // todo: poolg
                            subscriber.onNext(ByteBuffer.wrap(m.getBytes(StandardCharsets.UTF_8)));
                        } else { // missed
                            pending.incrementAndGet();
                        }
                    }
                } catch (final RuntimeException re) {
                    Logger.getLogger(getClass().getName()).log(SEVERE, re, re::getMessage);
                    subscriber.onError(re);
                }
            }

            @Override
            public void cancel() {
                lock.lock();
                try {
                    final var ref = sse;
                    if (ref != null) {
                        sse = null;
                        try {
                            ref.onComplete();
                        } catch (final RuntimeException re) {
                            Logger.getLogger(getClass().getName()).log(SEVERE, re, re::getMessage);
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        });
    }
}

