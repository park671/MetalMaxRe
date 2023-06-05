package me.afoolslove.metalmaxre;

import me.afoolslove.metalmaxre.editors.EditorManagerImpl;
import me.afoolslove.metalmaxre.editors.IRomEditor;
import me.afoolslove.metalmaxre.editors.map.IMapPropertiesEditor;
import me.afoolslove.metalmaxre.editors.map.MapProperties;
import me.afoolslove.metalmaxre.editors.map.world.IWorldMapEditor;
import me.afoolslove.metalmaxre.editors.map.world.WorldMapEditorImpl;
import me.afoolslove.metalmaxre.editors.monster.*;
import me.afoolslove.metalmaxre.editors.palette.Color;
import me.afoolslove.metalmaxre.helper.MonsterModelHelper;
import me.afoolslove.metalmaxre.utils.FileUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkMain {

    private static final String TAG = "ParkMain";

    public static void main(String[] args) throws Throwable {
        ParkMain parkMain = new ParkMain();
        MetalMaxRe metalMaxRe = parkMain.initRom();
//        parkMain.generateMonster(metalMaxRe);
//        Log.d(TAG, "finish generate Monster data");
//        parkMain.generateMap(metalMaxRe);
//        Log.d(TAG, "finish generate Map data");

        IMapPropertiesEditor mapPropertiesEditor = metalMaxRe.getEditorManager().getEditor(IMapPropertiesEditor.class);
        Map<Integer, MapProperties> mapPropertiesMap = mapPropertiesEditor.getMapProperties();
        for (Map.Entry<Integer, MapProperties> entry : mapPropertiesMap.entrySet()) {
            int mapId = (int) entry.getKey();
            Log.d(TAG, "---map:" + ((int) entry.getKey()) + "---");
            Log.d(TAG, "music:" + entry.getValue().getMusic());
            Log.d(TAG, "mw_offset:" + entry.getValue().movableWidthOffset);
            Log.d(TAG, "mh_offset:" + entry.getValue().movableHeightOffset);
            Log.d(TAG, "mw:" + entry.getValue().movableWidth);
            Log.d(TAG, "mh:" + entry.getValue().movableHeight);
            Log.d(TAG, "w:" + entry.getValue().getWidth());
            Log.d(TAG, "h:" + entry.getValue().getHeight());
            Log.d(TAG, "head:0x" + Integer.toHexString(entry.getValue().getHead()));

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unsigned char music_").append(mapId).append("=").append(entry.getValue().getMusic()).append(";\n");
            stringBuilder.append("unsigned char movableWidth_").append(mapId).append("=").append(entry.getValue().movableWidth).append(";\n");
            stringBuilder.append("unsigned char movableHeight_").append(mapId).append("=").append(entry.getValue().movableHeight).append(";\n");
            stringBuilder.append("unsigned char movableWidthOffset_").append(mapId).append("=").append(entry.getValue().movableWidthOffset).append(";\n");
            stringBuilder.append("unsigned char movableHeightOffset_").append(mapId).append("=").append(entry.getValue().movableHeightOffset).append(";\n");
            stringBuilder.append("unsigned char head_").append(mapId).append("=").append(entry.getValue().getHead()).append(";\n");
            FileUtils.writeFile("map/map_" + mapId + ".cpp", stringBuilder.toString());
        }
        System.exit(0);
    }

    private MetalMaxRe initRom() throws Throwable {
        RomBuffer romBuffer = new RomBuffer(RomVersion.getChinese(), (Path) null);
        MetalMaxRe metalMaxRe = new MetalMaxRe(romBuffer);
        var editorManager = new EditorManagerImpl(metalMaxRe);
        metalMaxRe.setEditorManager(editorManager);
        editorManager.registerDefaultEditors();
        editorManager.loadEditors().get();
        Map<Class<? extends IRomEditor>, IRomEditor> editors = editorManager.getEditors();
        Log.d(TAG, "rom loaded! editors = " + editors.size());
        return metalMaxRe;
    }

    private void generateMonster(MetalMaxRe metalMaxRe) {
        MonsterModelImpl monsterModel = metalMaxRe.getEditorManager().getEditor(MonsterModelImpl.class);
        MonsterEditorImpl monsterEditor = metalMaxRe.getEditorManager().getEditor(IMonsterEditor.class);
        HashMap<Integer, Monster> result = monsterEditor.getMonsters();
        Log.d(TAG, "result=" + result.size());
        StringBuilder monster_data_c = new StringBuilder("#include \"monster_data.h\"\n" +
                "#include \"../global.h\"\n");

        List<MonsterModel> monsterModels = monsterModel.getMonsterModels();

        for (int i = 0; i < monsterModels.size(); i++) {
            monster_data_c.append("#include \"monster_").append(i).append(".h\"\n");
        }
        monster_data_c.append("byte *monster_model[").append(monsterModels.size()).append("] = {\n");
        for (int i = 0; i < monsterModels.size(); i++) {
            if (i % 5 == 0) {
                monster_data_c.append("\n");
            }
            monster_data_c.append("(byte*)monster_").append(i).append(",");
        }
        monster_data_c.append("};\n");

        monster_data_c.append("int monster_count = ").append(monsterModels.size()).append(";\n");
        monster_data_c.append("int monster_size[").append(monsterModels.size()).append("*2] = {");


        for (int idx = 0; idx < monsterModels.size(); idx++) {
            MonsterModel model = monsterModels.get(idx);
            byte[][] modelData = MonsterModelHelper.generateMonsterModel(metalMaxRe, model);
            Color[][] modelPalette = MonsterModelHelper.generateMonsterModelPaletteFromModel(metalMaxRe, model);


            monster_data_c.append(modelData.length).append(",").append(modelData[0].length).append(",\n");


            StringBuilder monster_c_file = new StringBuilder();
            StringBuilder monster_head_file = new StringBuilder();

            monster_head_file.append("#ifndef METALMAX_MONSTER_").append(idx).append("_H\n");
            monster_head_file.append("#define METALMAX_MONSTER_").append(idx).append("_H\n");
            monster_head_file.append("extern unsigned char monster_").append(idx)
                    .append("[")
                    .append(modelData.length)
                    .append("][")
                    .append(modelData[0].length)
                    .append("];\n");

            monster_c_file.append("#include \"monster_")
                    .append(idx)
                    .append(".h\"\n")
                    .append("unsigned char monster_")
                    .append(idx)
                    .append("[")
                    .append(modelData.length)
                    .append("][")
                    .append(modelData[0].length)
                    .append("]={\n");
            for (int i = 0; i < modelData.length; i++) {
                monster_c_file.append("{");
                for (int j = 0; j < modelData[i].length; j++) {
                    byte point = modelData[i][j];
                    Color color = modelPalette[(point & 0B0011_0000) >>> 4][point & 0B0000_0011];
                    int colorIdx = addColor(color.getArgb());
                    monster_c_file.append(colorIdx + 33).append(",");
                }
                monster_c_file.append("},\n");
            }
            monster_c_file.append("};\n");
            FileUtils.writeFile("./monster/monster_" + idx + ".cpp", monster_c_file.toString());

            monster_head_file.append("#endif");
            FileUtils.writeFile("./monster/monster_" + idx + ".h", monster_head_file.toString());
        }
        monster_data_c.append("};\n");
        FileUtils.writeFile("./monster/monster_data.c", monster_data_c.toString());

        StringBuilder paletteStr = new StringBuilder("");
        for (int i = 0; i < palette.size(); i++) {
            if (i % 10 == 0) {
                paletteStr.append("\n");
            }
            paletteStr.append(palette.get(i)).append(",");
        }
        FileUtils.writeFile("./monster/palette.c", paletteStr.toString());
    }

    public void generateMap(MetalMaxRe metalMaxRe) {
        WorldMapEditorImpl worldMapEditor = metalMaxRe.getEditorManager().getEditor(IWorldMapEditor.class);
        byte[][] worldMap = worldMapEditor.getMap();
        Log.d(TAG, "height=" + worldMap.length + ", width=" + worldMap[0].length);
    }

    static List<Integer> palette = new ArrayList<>();

    private static int addColor(int argb) {
        if (!palette.contains(argb)) {
            palette.add(argb);
        }
        return palette.indexOf(argb);
    }


}
