package okhttp3;

import java.io.IOException;

public interface Call extends Cloneable {

    public interface Factory {
        Call newCall(Request request);
    }

    void cancel();

    Call clone();

    void enqueue(Callback callback);

    C3012Response execute() throws IOException;

    boolean isCanceled();

    boolean isExecuted();

    Request request();
}
