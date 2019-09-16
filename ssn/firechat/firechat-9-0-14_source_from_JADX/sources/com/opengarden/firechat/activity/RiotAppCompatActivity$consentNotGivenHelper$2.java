package com.opengarden.firechat.activity;

import com.opengarden.firechat.dialogs.ConsentNotGivenHelper;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "Lcom/opengarden/firechat/dialogs/ConsentNotGivenHelper;", "invoke"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: RiotAppCompatActivity.kt */
final class RiotAppCompatActivity$consentNotGivenHelper$2 extends Lambda implements Function0<ConsentNotGivenHelper> {
    final /* synthetic */ RiotAppCompatActivity this$0;

    RiotAppCompatActivity$consentNotGivenHelper$2(RiotAppCompatActivity riotAppCompatActivity) {
        this.this$0 = riotAppCompatActivity;
        super(0);
    }

    @NotNull
    public final ConsentNotGivenHelper invoke() {
        ConsentNotGivenHelper consentNotGivenHelper = new ConsentNotGivenHelper(this.this$0, this.this$0.savedInstanceState);
        this.this$0.addToRestorables(consentNotGivenHelper);
        return consentNotGivenHelper;
    }
}
