package kotlin.p009io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "Ljava/io/BufferedReader;", "invoke"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: kotlin.io.ConsoleKt$stdin$2 */
/* compiled from: Console.kt */
final class ConsoleKt$stdin$2 extends Lambda implements Function0<BufferedReader> {
    public static final ConsoleKt$stdin$2 INSTANCE = new ConsoleKt$stdin$2();

    ConsoleKt$stdin$2() {
        super(0);
    }

    @NotNull
    public final BufferedReader invoke() {
        return new BufferedReader(new InputStreamReader(new InputStream() {
            public int read() {
                return System.in.read();
            }

            public void reset() {
                System.in.reset();
            }

            public int read(@NotNull byte[] bArr) {
                Intrinsics.checkParameterIsNotNull(bArr, "b");
                return System.in.read(bArr);
            }

            public void close() {
                System.in.close();
            }

            public void mark(int i) {
                System.in.mark(i);
            }

            public long skip(long j) {
                return System.in.skip(j);
            }

            public int available() {
                return System.in.available();
            }

            public boolean markSupported() {
                return System.in.markSupported();
            }

            public int read(@NotNull byte[] bArr, int i, int i2) {
                Intrinsics.checkParameterIsNotNull(bArr, "b");
                return System.in.read(bArr, i, i2);
            }
        }));
    }
}
