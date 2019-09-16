package com.opengarden.firechat.util;

import android.support.annotation.StringRes;
import com.opengarden.firechat.C1299R;
import java.util.HashMap;
import java.util.Map;

public class SlashCommandsParser {
    private static final String CMD_DDG = "/ddg";
    private static final String CMD_IGNORE = "/ignore";
    private static final String CMD_TINT = "/tint";
    private static final String CMD_UNIGNORE = "/unignore";
    private static final String CMD_VERIFY = "/verify";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "SlashCommandsParser";

    public enum SlashCommand {
        EMOTE("/me", "<message>", C1299R.string.command_description_emote),
        BAN_USER("/ban", "<user-id>", C1299R.string.command_description_ban_user),
        UNBAN_USER("/unban", "<user-id>", C1299R.string.command_description_unban_user),
        SET_USER_POWER_LEVEL("/op", "<user-id> [<power-level>]", C1299R.string.command_description_op_user),
        RESET_USER_POWER_LEVEL("/deop", "<user-id>", C1299R.string.command_description_deop_user),
        INVITE("/invite", "<user-id>", C1299R.string.command_description_invite_user),
        JOIN_ROOM("/join", "<room-alias>", C1299R.string.command_description_join_room),
        PART("/part", "<room-alias>", C1299R.string.command_description_part_room),
        TOPIC("/topic", "<topic>", C1299R.string.command_description_topic),
        KICK_USER("/kick", "<user-id>", C1299R.string.command_description_kick_user),
        CHANGE_DISPLAY_NAME("/nick", "<display-name>", C1299R.string.command_description_nick),
        MARKDOWN("/markdown", "", C1299R.string.command_description_markdown),
        CLEAR_SCALAR_TOKEN("/clear_scalar_token", "", C1299R.string.command_description_clear_scalar_token);
        
        private static final Map<String, SlashCommand> lookup = null;
        private final String command;
        @StringRes
        private int description;
        private String parameter;

        static {
            int i;
            SlashCommand[] values;
            lookup = new HashMap();
            for (SlashCommand slashCommand : values()) {
                lookup.put(slashCommand.getCommand(), slashCommand);
            }
        }

        private SlashCommand(String str) {
            this.command = str;
        }

        private SlashCommand(String str, String str2, @StringRes int i) {
            this.command = str;
            this.parameter = str2;
            this.description = i;
        }

        public static SlashCommand get(String str) {
            return (SlashCommand) lookup.get(str);
        }

        public String getCommand() {
            return this.command;
        }

        public String getParam() {
            return this.parameter;
        }

        public int getDescription() {
            return this.description;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:116:0x02d0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean manageSplashCommand(final com.opengarden.firechat.activity.VectorRoomActivity r9, final com.opengarden.firechat.matrixsdk.MXSession r10, com.opengarden.firechat.matrixsdk.data.Room r11, final java.lang.String r12, java.lang.String r13, java.lang.String r14) {
        /*
            r0 = 0
            if (r9 == 0) goto L_0x02fa
            if (r10 == 0) goto L_0x02fa
            if (r11 != 0) goto L_0x0009
            goto L_0x02fa
        L_0x0009:
            r1 = 1
            if (r12 == 0) goto L_0x02f8
            java.lang.String r2 = "/"
            boolean r2 = r12.startsWith(r2)
            if (r2 == 0) goto L_0x02f8
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "manageSplashCommand : "
            r3.append(r4)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)
            int r2 = r12.length()
            if (r2 != r1) goto L_0x0031
            return r0
        L_0x0031:
            java.lang.String r2 = "/"
            r3 = 2
            java.lang.String r4 = r12.substring(r1, r3)
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto L_0x003f
            return r0
        L_0x003f:
            com.opengarden.firechat.util.SlashCommandsParser$1 r2 = new com.opengarden.firechat.util.SlashCommandsParser$1
            r2.<init>(r9, r12, r9)
            r4 = 0
            java.lang.String r5 = "\\s+"
            java.lang.String[] r5 = r12.split(r5)     // Catch:{ Exception -> 0x004c }
            goto L_0x0068
        L_0x004c:
            r5 = move-exception
            java.lang.String r6 = LOG_TAG
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "## manageSplashCommand() : split failed "
            r7.append(r8)
            java.lang.String r5 = r5.getMessage()
            r7.append(r5)
            java.lang.String r5 = r7.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r6, r5)
            r5 = r4
        L_0x0068:
            if (r5 == 0) goto L_0x02f7
            int r6 = r5.length
            if (r6 != 0) goto L_0x006f
            goto L_0x02f7
        L_0x006f:
            r6 = r5[r0]
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r7 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.CHANGE_DISPLAY_NAME
            java.lang.String r7 = r7.getCommand()
            boolean r7 = android.text.TextUtils.equals(r6, r7)
            if (r7 == 0) goto L_0x009f
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r11 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.CHANGE_DISPLAY_NAME
            java.lang.String r11 = r11.getCommand()
            int r11 = r11.length()
            java.lang.String r11 = r12.substring(r11)
            java.lang.String r11 = r11.trim()
            int r12 = r11.length()
            if (r12 <= 0) goto L_0x009c
            com.opengarden.firechat.matrixsdk.data.MyUser r10 = r10.getMyUser()
            r10.updateDisplayName(r11, r2)
        L_0x009c:
            r10 = 1
            goto L_0x02ce
        L_0x009f:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r7 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.TOPIC
            java.lang.String r7 = r7.getCommand()
            boolean r7 = android.text.TextUtils.equals(r6, r7)
            if (r7 == 0) goto L_0x00c7
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r10 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.TOPIC
            java.lang.String r10 = r10.getCommand()
            int r10 = r10.length()
            java.lang.String r10 = r12.substring(r10)
            java.lang.String r10 = r10.trim()
            int r12 = r10.length()
            if (r12 <= 0) goto L_0x009c
            r11.updateTopic(r10, r2)
            goto L_0x009c
        L_0x00c7:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r7 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.EMOTE
            java.lang.String r7 = r7.getCommand()
            boolean r7 = android.text.TextUtils.equals(r6, r7)
            if (r7 == 0) goto L_0x0113
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r10 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.EMOTE
            java.lang.String r10 = r10.getCommand()
            int r10 = r10.length()
            java.lang.String r10 = r12.substring(r10)
            java.lang.String r10 = r10.trim()
            int r11 = r12.length()
            if (r11 <= 0) goto L_0x009c
            if (r13 == 0) goto L_0x010f
            int r11 = r13.length()
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r12 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.EMOTE
            java.lang.String r12 = r12.getCommand()
            int r12 = r12.length()
            if (r11 <= r12) goto L_0x010f
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r11 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.EMOTE
            java.lang.String r11 = r11.getCommand()
            int r11 = r11.length()
            java.lang.String r11 = r13.substring(r11)
            r9.sendEmote(r10, r11, r14)
            goto L_0x009c
        L_0x010f:
            r9.sendEmote(r10, r13, r14)
            goto L_0x009c
        L_0x0113:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r13 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.JOIN_ROOM
            java.lang.String r13 = r13.getCommand()
            boolean r13 = android.text.TextUtils.equals(r6, r13)
            if (r13 == 0) goto L_0x0141
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r11 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.JOIN_ROOM
            java.lang.String r11 = r11.getCommand()
            int r11 = r11.length()
            java.lang.String r11 = r12.substring(r11)
            java.lang.String r11 = r11.trim()
            int r12 = r11.length()
            if (r12 <= 0) goto L_0x009c
            com.opengarden.firechat.util.SlashCommandsParser$2 r12 = new com.opengarden.firechat.util.SlashCommandsParser$2
            r12.<init>(r9, r10, r9)
            r10.joinRoom(r11, r12)
            goto L_0x009c
        L_0x0141:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r13 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.PART
            java.lang.String r13 = r13.getCommand()
            boolean r13 = android.text.TextUtils.equals(r6, r13)
            if (r13 == 0) goto L_0x01a3
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r11 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.PART
            java.lang.String r11 = r11.getCommand()
            int r11 = r11.length()
            java.lang.String r11 = r12.substring(r11)
            java.lang.String r11 = r11.trim()
            int r12 = r11.length()
            if (r12 <= 0) goto L_0x009c
            com.opengarden.firechat.matrixsdk.MXDataHandler r10 = r10.getDataHandler()
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r10 = r10.getStore()
            java.util.Collection r10 = r10.getRooms()
            java.util.Iterator r10 = r10.iterator()
        L_0x0175:
            boolean r12 = r10.hasNext()
            if (r12 == 0) goto L_0x019b
            java.lang.Object r12 = r10.next()
            com.opengarden.firechat.matrixsdk.data.Room r12 = (com.opengarden.firechat.matrixsdk.data.Room) r12
            com.opengarden.firechat.matrixsdk.data.RoomState r13 = r12.getLiveState()
            if (r13 == 0) goto L_0x0175
            java.lang.String r14 = r13.alias
            boolean r14 = android.text.TextUtils.equals(r14, r11)
            if (r14 == 0) goto L_0x0190
            goto L_0x019c
        L_0x0190:
            java.util.List r13 = r13.getAliases()
            int r13 = r13.indexOf(r11)
            if (r13 < 0) goto L_0x0175
            goto L_0x019c
        L_0x019b:
            r12 = r4
        L_0x019c:
            if (r12 == 0) goto L_0x009c
            r12.leave(r2)
            goto L_0x009c
        L_0x01a3:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r13 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.INVITE
            java.lang.String r13 = r13.getCommand()
            boolean r13 = android.text.TextUtils.equals(r6, r13)
            if (r13 == 0) goto L_0x01b9
            int r10 = r5.length
            if (r10 < r3) goto L_0x009c
            r10 = r5[r1]
            r11.invite(r10, r2)
            goto L_0x009c
        L_0x01b9:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r13 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.KICK_USER
            java.lang.String r13 = r13.getCommand()
            boolean r13 = android.text.TextUtils.equals(r6, r13)
            if (r13 == 0) goto L_0x01cf
            int r10 = r5.length
            if (r10 < r3) goto L_0x009c
            r10 = r5[r1]
            r11.kick(r10, r2)
            goto L_0x009c
        L_0x01cf:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r13 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.BAN_USER
            java.lang.String r13 = r13.getCommand()
            boolean r13 = android.text.TextUtils.equals(r6, r13)
            if (r13 == 0) goto L_0x020c
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r10 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.BAN_USER
            java.lang.String r10 = r10.getCommand()
            int r10 = r10.length()
            java.lang.String r10 = r12.substring(r10)
            java.lang.String r10 = r10.trim()
            java.lang.String r12 = " "
            java.lang.String[] r12 = r10.split(r12)
            r12 = r12[r0]
            int r13 = r12.length()
            java.lang.String r10 = r10.substring(r13)
            java.lang.String r10 = r10.trim()
            int r13 = r12.length()
            if (r13 <= 0) goto L_0x009c
            r11.ban(r12, r10, r2)
            goto L_0x009c
        L_0x020c:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r12 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.UNBAN_USER
            java.lang.String r12 = r12.getCommand()
            boolean r12 = android.text.TextUtils.equals(r6, r12)
            if (r12 == 0) goto L_0x0222
            int r10 = r5.length
            if (r10 < r3) goto L_0x009c
            r10 = r5[r1]
            r11.unban(r10, r2)
            goto L_0x009c
        L_0x0222:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r12 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.SET_USER_POWER_LEVEL
            java.lang.String r12 = r12.getCommand()
            boolean r12 = android.text.TextUtils.equals(r6, r12)
            if (r12 == 0) goto L_0x0268
            int r10 = r5.length
            r12 = 3
            if (r10 < r12) goto L_0x009c
            r10 = r5[r1]
            r12 = r5[r3]
            int r13 = r10.length()     // Catch:{ Exception -> 0x024b }
            if (r13 <= 0) goto L_0x009c
            int r13 = r12.length()     // Catch:{ Exception -> 0x024b }
            if (r13 <= 0) goto L_0x009c
            int r12 = java.lang.Integer.parseInt(r12)     // Catch:{ Exception -> 0x024b }
            r11.updateUserPowerLevels(r10, r12, r2)     // Catch:{ Exception -> 0x024b }
            goto L_0x009c
        L_0x024b:
            r10 = move-exception
            java.lang.String r11 = LOG_TAG
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "mRoom.updateUserPowerLevels "
            r12.append(r13)
            java.lang.String r10 = r10.getMessage()
            r12.append(r10)
            java.lang.String r10 = r12.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r11, r10)
            goto L_0x009c
        L_0x0268:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r12 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.RESET_USER_POWER_LEVEL
            java.lang.String r12 = r12.getCommand()
            boolean r12 = android.text.TextUtils.equals(r6, r12)
            if (r12 == 0) goto L_0x027e
            int r10 = r5.length
            if (r10 < r3) goto L_0x009c
            r10 = r5[r1]
            r11.updateUserPowerLevels(r10, r0, r2)
            goto L_0x009c
        L_0x027e:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r11 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.MARKDOWN
            java.lang.String r11 = r11.getCommand()
            boolean r11 = android.text.TextUtils.equals(r6, r11)
            if (r11 == 0) goto L_0x02b3
            int r10 = r5.length
            if (r10 < r3) goto L_0x009c
            r10 = r5[r1]
            java.lang.String r11 = "on"
            boolean r10 = android.text.TextUtils.equals(r10, r11)
            if (r10 == 0) goto L_0x02a0
            com.opengarden.firechat.VectorApp r10 = com.opengarden.firechat.VectorApp.getInstance()
            com.opengarden.firechat.util.PreferencesManager.setMarkdownEnabled(r10, r1)
            goto L_0x009c
        L_0x02a0:
            r10 = r5[r1]
            java.lang.String r11 = "off"
            boolean r10 = android.text.TextUtils.equals(r10, r11)
            if (r10 == 0) goto L_0x009c
            com.opengarden.firechat.VectorApp r10 = com.opengarden.firechat.VectorApp.getInstance()
            com.opengarden.firechat.util.PreferencesManager.setMarkdownEnabled(r10, r0)
            goto L_0x009c
        L_0x02b3:
            com.opengarden.firechat.util.SlashCommandsParser$SlashCommand r11 = com.opengarden.firechat.util.SlashCommandsParser.SlashCommand.CLEAR_SCALAR_TOKEN
            java.lang.String r11 = r11.getCommand()
            boolean r11 = android.text.TextUtils.equals(r6, r11)
            if (r11 == 0) goto L_0x02cd
            com.opengarden.firechat.widgets.WidgetsManager.clearScalarToken(r9, r10)
            java.lang.String r10 = "Scalar token cleared"
            android.widget.Toast r10 = android.widget.Toast.makeText(r9, r10, r0)
            r10.show()
            goto L_0x009c
        L_0x02cd:
            r10 = 0
        L_0x02ce:
            if (r10 != 0) goto L_0x02f9
            android.app.AlertDialog$Builder r10 = new android.app.AlertDialog$Builder
            r10.<init>(r9)
            r11 = 2131689635(0x7f0f00a3, float:1.900829E38)
            android.app.AlertDialog$Builder r10 = r10.setTitle(r11)
            r11 = 2131690336(0x7f0f0360, float:1.9009713E38)
            java.lang.Object[] r12 = new java.lang.Object[r1]
            r12[r0] = r6
            java.lang.String r9 = r9.getString(r11, r12)
            android.app.AlertDialog$Builder r9 = r10.setMessage(r9)
            r10 = 2131689926(0x7f0f01c6, float:1.9008881E38)
            android.app.AlertDialog$Builder r9 = r9.setPositiveButton(r10, r4)
            r9.show()
            r10 = 1
            goto L_0x02f9
        L_0x02f7:
            return r0
        L_0x02f8:
            r10 = 0
        L_0x02f9:
            return r10
        L_0x02fa:
            java.lang.String r9 = LOG_TAG
            java.lang.String r10 = "manageSplashCommand : invalid parameters"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r9, r10)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.SlashCommandsParser.manageSplashCommand(com.opengarden.firechat.activity.VectorRoomActivity, com.opengarden.firechat.matrixsdk.MXSession, com.opengarden.firechat.matrixsdk.data.Room, java.lang.String, java.lang.String, java.lang.String):boolean");
    }
}
