package com.opengarden.firechat.adapters;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ImageView;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.contacts.Contact;
import com.opengarden.firechat.contacts.Contact.MXID;
import com.opengarden.firechat.contacts.PIDsRetriever;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.util.VectorUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class ParticipantAdapterItem implements Serializable {
    private static final Pattern FACEBOOK_EMAIL_ADDRESS = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@facebook.com");
    public static final Comparator<ParticipantAdapterItem> alphaComparator = new Comparator<ParticipantAdapterItem>() {
        public int compare(ParticipantAdapterItem participantAdapterItem, ParticipantAdapterItem participantAdapterItem2) {
            String comparisonDisplayName = participantAdapterItem.getComparisonDisplayName();
            String comparisonDisplayName2 = participantAdapterItem2.getComparisonDisplayName();
            if (comparisonDisplayName == null) {
                return -1;
            }
            if (comparisonDisplayName2 == null) {
                return 1;
            }
            return String.CASE_INSENSITIVE_ORDER.compare(comparisonDisplayName, comparisonDisplayName2);
        }
    };
    private static final List<Pattern> mBlackedListEmails = Collections.singletonList(FACEBOOK_EMAIL_ADDRESS);
    private static final String mTrimRegEx = "[_!~`@#$%^&*\\-+();:=\\{\\}\\[\\],.<>?]";
    public String mAvatarUrl;
    private String mComparisonDisplayName;
    public Contact mContact;
    public String mDisplayName;
    private ArrayList<String> mDisplayNameComponents;
    public boolean mIsValid = true;
    private String mLowerCaseDisplayName;
    private String mLowerCaseMatrixId;
    public RoomMember mRoomMember;
    public String mUserId;

    public ParticipantAdapterItem(RoomMember roomMember) {
        this.mDisplayName = roomMember.getName();
        this.mAvatarUrl = roomMember.getAvatarUrl();
        this.mUserId = roomMember.getUserId();
        this.mRoomMember = roomMember;
        this.mContact = null;
        initSearchByPatternFields();
    }

    public ParticipantAdapterItem(User user) {
        this.mDisplayName = TextUtils.isEmpty(user.displayname) ? user.user_id : user.displayname;
        this.mUserId = user.user_id;
        this.mAvatarUrl = user.getAvatarUrl();
        initSearchByPatternFields();
    }

    public ParticipantAdapterItem(Contact contact) {
        this.mDisplayName = contact.getDisplayName();
        if (TextUtils.isEmpty(this.mDisplayName)) {
            this.mDisplayName = contact.getContactId();
        }
        this.mUserId = null;
        this.mRoomMember = null;
        this.mContact = contact;
        initSearchByPatternFields();
    }

    public ParticipantAdapterItem(String str, String str2, String str3, boolean z) {
        this.mDisplayName = str;
        this.mAvatarUrl = str2;
        this.mUserId = str3;
        this.mIsValid = z;
        initSearchByPatternFields();
    }

    private void initSearchByPatternFields() {
        if (!TextUtils.isEmpty(this.mDisplayName)) {
            this.mLowerCaseDisplayName = this.mDisplayName.toLowerCase(VectorApp.getApplicationLocale());
        }
        if (!TextUtils.isEmpty(this.mUserId)) {
            this.mLowerCaseMatrixId = this.mUserId.toLowerCase(VectorApp.getApplicationLocale());
        }
    }

    public String getComparisonDisplayName() {
        if (this.mComparisonDisplayName == null) {
            if (!TextUtils.isEmpty(this.mDisplayName)) {
                this.mComparisonDisplayName = this.mDisplayName;
            } else {
                this.mComparisonDisplayName = this.mUserId;
            }
            if (this.mComparisonDisplayName == null) {
                this.mComparisonDisplayName = "";
            } else {
                this.mComparisonDisplayName = this.mComparisonDisplayName.replaceAll(mTrimRegEx, "");
            }
        }
        return this.mComparisonDisplayName;
    }

    public static Comparator<ParticipantAdapterItem> getComparator(MXSession mXSession) {
        final IMXStore store = mXSession.getDataHandler().getStore();
        return new Comparator<ParticipantAdapterItem>() {
            final HashSet<String> mUnknownUsers = new HashSet<>();
            final Map<String, User> mUsersMap = new HashMap();

            private int alphaComparator(String str, String str2) {
                if (str == null) {
                    return -1;
                }
                if (str2 == null) {
                    return 1;
                }
                return String.CASE_INSENSITIVE_ORDER.compare(str, str2);
            }

            private User getUser(String str) {
                if (this.mUsersMap.containsKey(str)) {
                    return (User) this.mUsersMap.get(str);
                }
                if (this.mUnknownUsers.contains(str)) {
                    return null;
                }
                User user = store.getUser(str);
                if (user == null) {
                    this.mUnknownUsers.add(str);
                } else {
                    this.mUsersMap.put(str, user);
                }
                return user;
            }

            public int compare(ParticipantAdapterItem participantAdapterItem, ParticipantAdapterItem participantAdapterItem2) {
                User user = getUser(participantAdapterItem.mUserId);
                User user2 = getUser(participantAdapterItem2.mUserId);
                String comparisonDisplayName = participantAdapterItem.getComparisonDisplayName();
                String comparisonDisplayName2 = participantAdapterItem2.getComparisonDisplayName();
                boolean z = false;
                boolean booleanValue = (user == null || user.currently_active == null) ? false : user.currently_active.booleanValue();
                if (!(user2 == null || user2.currently_active == null)) {
                    z = user2.currently_active.booleanValue();
                }
                if (user == null && user2 == null) {
                    return alphaComparator(comparisonDisplayName, comparisonDisplayName2);
                }
                int i = 1;
                if (user != null && user2 == null) {
                    return 1;
                }
                if (user == null && user2 != null) {
                    return -1;
                }
                if (booleanValue && z) {
                    return alphaComparator(comparisonDisplayName, comparisonDisplayName2);
                }
                if (booleanValue && !z) {
                    return -1;
                }
                if (!booleanValue && z) {
                    return 1;
                }
                long absoluteLastActiveAgo = user != null ? user.getAbsoluteLastActiveAgo() : 0;
                long absoluteLastActiveAgo2 = user2 != null ? user2.getAbsoluteLastActiveAgo() : 0;
                long j = absoluteLastActiveAgo - absoluteLastActiveAgo2;
                if (j == 0) {
                    return alphaComparator(comparisonDisplayName, comparisonDisplayName2);
                }
                if (0 == absoluteLastActiveAgo) {
                    return 1;
                }
                if (0 == absoluteLastActiveAgo2) {
                    return -1;
                }
                if (j <= 0) {
                    i = -1;
                }
                return i;
            }
        };
    }

    public boolean contains(String str) {
        boolean z = false;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (!TextUtils.isEmpty(this.mLowerCaseDisplayName)) {
            z = this.mLowerCaseDisplayName.contains(str);
        }
        if (!z && !TextUtils.isEmpty(this.mLowerCaseMatrixId)) {
            z = this.mLowerCaseMatrixId.contains(str);
        }
        if (!z && this.mContact != null) {
            z = this.mContact.contains(str);
        }
        return z;
    }

    public boolean startsWith(String str) {
        boolean z = false;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (!TextUtils.isEmpty(this.mDisplayName)) {
            if (this.mLowerCaseDisplayName != null && this.mLowerCaseDisplayName.startsWith(str)) {
                return true;
            }
            if (this.mDisplayNameComponents == null) {
                String[] split = this.mDisplayName.split(StringUtils.SPACE);
                this.mDisplayNameComponents = new ArrayList<>();
                if (split.length > 0) {
                    for (String trim : split) {
                        this.mDisplayNameComponents.add(trim.trim().toLowerCase(VectorApp.getApplicationLocale()));
                    }
                }
            }
            Iterator it = this.mDisplayNameComponents.iterator();
            while (it.hasNext()) {
                if (((String) it.next()).startsWith(str)) {
                    return true;
                }
            }
        }
        if (!TextUtils.isEmpty(this.mLowerCaseMatrixId)) {
            String str2 = this.mLowerCaseMatrixId;
            StringBuilder sb = new StringBuilder();
            sb.append(str.startsWith("@") ? "" : "@");
            sb.append(str);
            if (str2.startsWith(sb.toString())) {
                return true;
            }
        }
        if (this.mContact != null && this.mContact.startsWith(str)) {
            z = true;
        }
        return z;
    }

    public Bitmap getAvatarBitmap() {
        if (this.mContact != null) {
            return this.mContact.getThumbnail(VectorApp.getInstance());
        }
        return null;
    }

    public void displayAvatar(MXSession mXSession, ImageView imageView) {
        if (imageView != null) {
            if (getAvatarBitmap() != null) {
                imageView.setImageBitmap(getAvatarBitmap());
            } else if ((this.mUserId != null && Patterns.EMAIL_ADDRESS.matcher(this.mUserId).matches()) || !this.mIsValid) {
                imageView.setImageBitmap(VectorUtils.getAvatar(imageView.getContext(), VectorUtils.getAvatarColor(this.mIsValid ? this.mUserId : ""), "@@", true));
            } else if (TextUtils.isEmpty(this.mUserId)) {
                VectorUtils.loadUserAvatar(imageView.getContext(), mXSession, imageView, this.mAvatarUrl, this.mDisplayName, this.mDisplayName);
            } else {
                if (TextUtils.equals(this.mUserId, this.mDisplayName) || TextUtils.isEmpty(this.mAvatarUrl)) {
                    IMXStore store = mXSession.getDataHandler().getStore();
                    if (store != null) {
                        User user = store.getUser(this.mUserId);
                        if (user != null) {
                            if (TextUtils.equals(this.mUserId, this.mDisplayName) && !TextUtils.isEmpty(user.displayname)) {
                                this.mDisplayName = user.displayname;
                            }
                            if (this.mAvatarUrl == null) {
                                this.mAvatarUrl = user.avatar_url;
                            }
                        }
                    }
                }
                VectorUtils.loadUserAvatar(imageView.getContext(), mXSession, imageView, this.mAvatarUrl, this.mUserId, this.mDisplayName);
            }
        }
    }

    public String getUniqueDisplayName(List<String> list) {
        String str = this.mDisplayName;
        if (this.mContact == null) {
            String lowerCase = str.toLowerCase(VectorApp.getApplicationLocale());
            int i = -1;
            if (list != null) {
                int indexOf = list.indexOf(lowerCase);
                if (indexOf < 0 || indexOf != list.lastIndexOf(lowerCase)) {
                    i = indexOf;
                }
            }
            if (i < 0) {
                return str;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(" (");
            sb.append(this.mUserId);
            sb.append(")");
            return sb.toString();
        } else if (!MXSession.PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER.matcher(this.mUserId).matches()) {
            return str;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(" (");
            sb2.append(this.mUserId);
            sb2.append(")");
            return sb2.toString();
        }
    }

    public boolean retrievePids() {
        if (Patterns.EMAIL_ADDRESS.matcher(this.mUserId).matches()) {
            if (this.mContact != null) {
                this.mContact.refreshMatridIds();
            }
            MXID mxid = PIDsRetriever.getInstance().getMXID(this.mUserId);
            if (mxid != null) {
                this.mUserId = mxid.mMatrixId;
                return true;
            }
        }
        return false;
    }

    public static boolean isBlackedListed(String str) {
        for (int i = 0; i < mBlackedListEmails.size(); i++) {
            if (((Pattern) mBlackedListEmails.get(i)).matcher(str).matches()) {
                return true;
            }
        }
        return !Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }
}
