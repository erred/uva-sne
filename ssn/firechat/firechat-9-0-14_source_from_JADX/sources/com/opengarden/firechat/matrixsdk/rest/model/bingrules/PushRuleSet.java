package com.opengarden.firechat.matrixsdk.rest.model.bingrules;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class PushRuleSet {
    public List<ContentRule> content = new ArrayList();
    public List<BingRule> override = new ArrayList();
    public List<BingRule> room = new ArrayList();
    public List<BingRule> sender = new ArrayList();
    public List<BingRule> underride = new ArrayList();

    private BingRule findRule(List<BingRule> list, String str) {
        for (BingRule bingRule : list) {
            if (TextUtils.equals(str, bingRule.ruleId)) {
                return bingRule;
            }
        }
        return null;
    }

    private List<BingRule> getBingRulesList(String str) {
        if (BingRule.KIND_OVERRIDE.equals(str)) {
            return this.override;
        }
        if (BingRule.KIND_ROOM.equals(str)) {
            return this.room;
        }
        if (BingRule.KIND_SENDER.equals(str)) {
            return this.sender;
        }
        if (BingRule.KIND_UNDERRIDE.equals(str)) {
            return this.underride;
        }
        return null;
    }

    public void addAtTop(BingRule bingRule) {
        if (!TextUtils.equals("content", bingRule.kind)) {
            List bingRulesList = getBingRulesList(bingRule.kind);
            if (bingRulesList != null) {
                bingRulesList.add(0, bingRule);
            }
        } else if (this.content != null && (bingRule instanceof ContentRule)) {
            this.content.add(0, (ContentRule) bingRule);
        }
    }

    public boolean remove(BingRule bingRule) {
        if (!"content".equals(bingRule.kind)) {
            List bingRulesList = getBingRulesList(bingRule.kind);
            if (bingRulesList != null) {
                return bingRulesList.remove(bingRule);
            }
        } else if (this.content != null) {
            return this.content.remove(bingRule);
        }
        return false;
    }

    private BingRule findContentRule(List<ContentRule> list, String str) {
        for (BingRule bingRule : list) {
            if (TextUtils.equals(str, bingRule.ruleId)) {
                return bingRule;
            }
        }
        return null;
    }

    public BingRule findDefaultRule(String str) {
        if (str == null) {
            return null;
        }
        if (TextUtils.equals(BingRule.RULE_ID_CONTAIN_USER_NAME, str)) {
            return findContentRule(this.content, str);
        }
        BingRule findRule = findRule(this.override, str);
        return findRule == null ? findRule(this.underride, str) : findRule;
    }

    public List<BingRule> getContentRules() {
        ArrayList arrayList = new ArrayList();
        if (this.content != null) {
            for (BingRule bingRule : this.content) {
                if (!bingRule.ruleId.startsWith(".m.")) {
                    arrayList.add(bingRule);
                }
            }
        }
        return arrayList;
    }

    public List<BingRule> getRoomRules() {
        if (this.room == null) {
            return new ArrayList();
        }
        return this.room;
    }

    public List<BingRule> getSenderRules() {
        if (this.sender == null) {
            return new ArrayList();
        }
        return this.sender;
    }
}
