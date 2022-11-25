package com.sierra.agi.view;

import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.view.ScriptBuffer.ScriptBufferEvent;

import java.util.ArrayList;

import static com.sierra.agi.logic.LogicVariables.FLAG_SCRIPT_BLOCKED;

public class ScriptBuffer {
    public enum ScriptBufferEventType {
        LoadLogic,
        LoadView,
        LoadPic,
        LoadSound,
        DrawPic,
        AddToPic,
        DiscardPic,
        DiscardView,
        OverlayPic
    }

    public class ScriptBufferEvent {
        public ScriptBufferEventType type;
        public int resourceNumber;
        public byte[] data;

        public ScriptBufferEvent(ScriptBufferEventType type, int resourceNumber, byte[] data) {
            this.type = type;
            this.resourceNumber = resourceNumber;
            this.data = data;
        }
    }

    private LogicContext logicContext;
    private ArrayList<ScriptBufferEvent> events;
    private boolean doScript;
    private int maxScript;
    private int scriptSize;
    private int savedScript;

    public ScriptBuffer(LogicContext logicContext) {
        // Default script size is 50 according to original AGI specs.
        this.scriptSize = 50;
        this.events = new ArrayList();
        this.logicContext = logicContext;
        this.initScript();
    }

    public void scriptOff() {
        doScript = false;
    }

    public void scriptOn() {
        doScript = true;
    }

    public void initScript() {
        this.events.clear();
    }

    public int getScriptEntries() {
        int count = 0;
        for (ScriptBufferEvent e : events) {
            // in AGI, the add.to.pic script event consist of 4 entries
            // (who, action, loop #, view #, X, Y, cel #, priority)
            // the rest of the events are just 1 entry (who, action)
            if (e.type == ScriptBufferEventType.AddToPic) {
                count += 4;
            } else {
                count += 1;
            }
        }
        return count;
    }

    public void addScript(ScriptBufferEventType action, int who, byte[] data) {
        if (this.logicContext.getFlag(FLAG_SCRIPT_BLOCKED)) {
            return;
        }

        if (doScript) {
            if (this.events.size() >= this.scriptSize) {
                // TODO: Error. Error(11, maxScript);
                return;
            } else {
                this.events.add(new ScriptBufferEvent(action, who, data));
            }
        }

        if (this.events.size() > this.maxScript) {
            this.maxScript = this.events.size();
        }
    }

    public void setScriptSize(int scriptSize) {
        this.scriptSize = scriptSize;
        this.events.clear();
    }

    public void pushScript() {
        this.savedScript = this.events.size();
    }

    public void popScript() {
        if (this.events.size() > this.savedScript) {
            int count = this.events.size() - this.savedScript;
            int index = this.savedScript;

            this.events.subList(index, index + count).clear();
        }
    }

    /// <summary>
    /// Returns the script event buffer as a raw byte array.
    /// </summary>
    /// <returns></returns>
    public byte[] encode() {
        // Each script entry is two bytes long.
        /*
        MemoryStream stream = new MemoryStream(this.ScriptSize * 2);

        foreach(ScriptBufferEvent e in Events)
        {
            stream.WriteByte((byte) (e.type));
            stream.WriteByte((byte) e.resourceNumber);
            if (e.data != null) {
                stream.Write(e.data, 0, e.data.Length);
            }
        }

        // We deliberately use GetBuffer rather than ToArray so that we get the
        // unused part as well.
        return stream.GetBuffer();

         */
        // TODO
        return new byte[] {};
    }

    /// <summary>
    /// Add an event to the script buffer without checking NO_SCRIPT flag. Used primarily by restore save game function.
    /// </summary>
    /// <param name="action"></param>
    /// <param name="who"></param>
    public void restoreScript(ScriptBufferEventType action, int who, byte[] data) {
        this.events.add(new ScriptBufferEvent(action, who, data));

        if (this.events.size() > this.maxScript) {
            this.maxScript = this.events.size();
        }
    }

    public int getScriptSize() {
        return scriptSize;
    }
}
