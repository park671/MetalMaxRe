package me.afoolslove.metalmaxre;

import me.afoolslove.metalmaxre.editors.EditorManagerImpl;
import me.afoolslove.metalmaxre.editors.IRomEditor;
import me.afoolslove.metalmaxre.editors.monster.IMonsterEditor;
import me.afoolslove.metalmaxre.editors.monster.Monster;
import me.afoolslove.metalmaxre.editors.monster.MonsterEditorImpl;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ParkMain {

    private static final String TAG = "ParkMain";

    public static void main(String[] args) throws Throwable {
        RomBuffer romBuffer = new RomBuffer(RomVersion.getChinese(), (Path) null);
        MetalMaxRe metalMaxRe = new MetalMaxRe(romBuffer);
        var editorManager = new EditorManagerImpl(metalMaxRe);
        metalMaxRe.setEditorManager(editorManager);
        editorManager.registerDefaultEditors();
        editorManager.loadEditors().get();
        Map<Class<? extends IRomEditor>, IRomEditor> editors = editorManager.getEditors();
        Log.d(TAG, "rom loaded! editors = " + editors.size());
        MonsterEditorImpl monsterEditor = editorManager.getEditor(IMonsterEditor.class);
        HashMap<Integer, Monster> result = monsterEditor.getMonsters();
        Log.d(TAG, "result=" + result.size());
    }
}
