package retrofit2;

public interface Callback<T> {
    void onFailure(Call<T> call, Throwable th);

    void onResponse(Call<T> call, C3224Response<T> response);
}
