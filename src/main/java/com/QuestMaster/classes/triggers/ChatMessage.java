package com.QuestMaster.classes.triggers;

import com.QuestMaster.classes.Trigger;
import net.minecraft.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatMessage extends Trigger {
    public HashMap<String, Boolean> patterns;
    public int amountNeeded = 1;
    public boolean colorCodes = true;

    public ChatMessage(List<String> patterns, int amountNeeded, boolean colorCodes) {
        this.patterns = new HashMap<String, Boolean>() {{
            patterns.forEach(val -> put(val, false));
        }};
        this.amountNeeded = amountNeeded;
        this.colorCodes = colorCodes;
    }

    public ChatMessage(String pattern, boolean colorCodes) {
        this.patterns = new HashMap<String, Boolean>() {{
            put(pattern, false);
        }};
        this.colorCodes = colorCodes;
    }

    public void reset() {
        for (Map.Entry<String, Boolean> c : this.patterns.entrySet()) {
            if (c.getValue()) this.patterns.put(c.getKey(), false);
        }
    }

    @Override
    public boolean checkTrigger(String m) {
        String message = this.colorCodes ? m : StringUtils.stripControlCodes(m);
        int found = 0;
        for (Map.Entry<String, Boolean> pattern : this.patterns.entrySet()) {
            if (!pattern.getValue()) {
                Matcher matcher = Pattern.compile(pattern.getKey()).matcher(message);
                if (matcher.find()) {
                    this.patterns.put(pattern.getKey(), true);
                    found++;
                }
            } else found++;
        }
        if (found >= amountNeeded) {
            reset();
            return true;
        }
        return false;
    }
}
