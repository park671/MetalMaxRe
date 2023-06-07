package me.afoolslove.metalmaxre;

import me.afoolslove.metalmaxre.editors.EditorManagerImpl;
import me.afoolslove.metalmaxre.editors.IRomEditor;
import me.afoolslove.metalmaxre.editors.computer.Computer;
import me.afoolslove.metalmaxre.editors.computer.IComputerEditor;
import me.afoolslove.metalmaxre.editors.computer.shop.IShopEditor;
import me.afoolslove.metalmaxre.editors.computer.shop.VendorItem;
import me.afoolslove.metalmaxre.editors.computer.shop.VendorItemList;
import me.afoolslove.metalmaxre.editors.map.IMapPropertiesEditor;
import me.afoolslove.metalmaxre.editors.map.MapProperties;
import me.afoolslove.metalmaxre.editors.map.world.IWorldMapEditor;
import me.afoolslove.metalmaxre.editors.map.world.WorldMapEditorImpl;
import me.afoolslove.metalmaxre.editors.monster.*;
import me.afoolslove.metalmaxre.editors.palette.Color;
import me.afoolslove.metalmaxre.helper.MonsterModelHelper;
import me.afoolslove.metalmaxre.utils.FileUtils;

import java.lang.reflect.Field;
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
//        parkMain.generateMapProperties(metalMaxRe);
//        Log.d(TAG, "finish generate Map properties data");
//        generateMonsterGroup(metalMaxRe);
//        generateComputorData(metalMaxRe);
//        generateShopData(metalMaxRe);
        generateVenderData(metalMaxRe);
        System.exit(0);
    }

    private static void generateShopData(MetalMaxRe metalMaxRe) {
        IShopEditor shopEditor = metalMaxRe.getEditorManager().getEditor(IShopEditor.class);
        Map<Integer, List<Byte>> shopLists = shopEditor.getShopLists();
        StringBuilder shopSb = new StringBuilder("byte *stores_data[").append(shopLists.size()).append("]={\n");
        StringBuilder shopItemSb = new StringBuilder();
        StringBuilder shopSizeSb = new StringBuilder("byte stores_size[").append(shopLists.size()).append("]={\n");
        for (int i = 0; i < shopLists.size(); i++) {
            if (i % 10 == 0) {
                shopSb.append("\n");
                shopSizeSb.append("\n");
            }
            List<Byte> thisStoresItems = shopLists.get(i);
            shopSb.append("store_").append(i).append(",");
            shopItemSb.append("byte store_").append(i).append("[").append(thisStoresItems.size()).append("]={");
            shopSizeSb.append(thisStoresItems.size()).append(",");
            for (int j = 0; j < thisStoresItems.size(); j++) {
                shopItemSb.append(thisStoresItems.get(j) & 0xFF).append(",");
            }
            shopItemSb.append("};\n");
        }
        shopSb.append("};\n");
        shopSizeSb.append("};\n");
        shopItemSb.append("\n").append(shopSb).append("\n").append(shopSizeSb);
        Log.d(TAG, shopItemSb.toString());
    }

    private static void generateVenderData(MetalMaxRe metalMaxRe) {
        IShopEditor shopEditor = metalMaxRe.getEditorManager().getEditor(IShopEditor.class);
        List<VendorItemList> vendorItemLists = shopEditor.getVendorItemLists();
        StringBuilder shopSb = new StringBuilder("byte *vendor_data[").append(vendorItemLists.size()).append("][6*2+1]={\n");
        for (int i = 0; i < vendorItemLists.size(); i++) {
            VendorItemList thisStoresItems = vendorItemLists.get(i);
            shopSb.append("{").append("/*award*/").append(thisStoresItems.intAward()).append(",");
            for (int j = 0; j < thisStoresItems.size(); j++) {
                VendorItem vendorItem = thisStoresItems.get(j);
                shopSb.append(vendorItem.item & 0xFF).append(",");
                shopSb.append(vendorItem.count & 0xFF).append(",");
            }
            shopSb.append("},\n");
        }
        shopSb.append("};\n");
        Log.d(TAG, shopSb.toString());
    }

    private static void generateComputorData(MetalMaxRe metalMaxRe) {
        IComputerEditor iComputerEditor = metalMaxRe.getEditorManager().getEditor(IComputerEditor.class);
        List<Computer> computers = iComputerEditor.getComputers();
        StringBuilder computerSb = new StringBuilder("unsigned char computers[").append(computers.size()).append("]={\n");
        for (int i = 0; i < computers.size(); i++) {
            Computer computer = computers.get(i);
            int x = computer.getX() & 0xff;
            int y = computer.getY() & 0xff;
            int mapId = computer.getMap() & 0xff;
            int type = computer.getType() & 0xff;
            computerSb.append(x).append(",").append(y).append(",").append(mapId).append(",").append(type).append(",\n");
        }
        computerSb.append("};\n");
    }

    static Class<?> monsterClazz = Monster.class;

    private static void generateMonsterGroup(MetalMaxRe metalMaxRe) throws IllegalAccessException {
        Field[] fields = monsterClazz.getDeclaredFields();
        StringBuilder fieldNames = new StringBuilder("");
        for (Field field : fields) {
            fieldNames.append(field.getName()).append("\t");
        }

        StringBuilder monsterValues = new StringBuilder("#define EMPTY 255\nunsigned char monster_propertys[131]={\n");
//        StringBuilder monsterValues = new StringBuilder();
        IMonsterEditor monsterEditor = metalMaxRe.getEditorManager().getEditor(IMonsterEditor.class);
        IMapPropertiesEditor mapPropertiesEditor = metalMaxRe.getEditorManager().getEditor(IMapPropertiesEditor.class);
        Map<Integer, Monster> monsters = monsterEditor.getMonsters();
        for (Map.Entry<Integer, Monster> monsterEntry : monsters.entrySet()) {
            monsterEntry.getKey();
            Monster monster = monsterEntry.getValue();

            for (Field field : fields) {
                field.setAccessible(true);
                Byte value = (Byte) field.get(monster);
//                monsterValues.append(value).append(",\t");
                if (value != null) {
                    int num = value & 0xff;
                    monsterValues.append(num).append(",");
                } else {
                    monsterValues.append(255).append(",");
                }
            }
            monsterValues.append("\n");
        }
        monsterValues.append("};\n");
        fieldNames.append("\n").append(monsterValues);
        Log.d(TAG, fieldNames.toString());
        FileUtils.writeFile("monster_data/monster_data.cpp", fieldNames.toString());


        //-------
        // 世界地图怪物组合
        List<Byte> worldMapRealms = monsterEditor.getWorldMapRealms();

        MonsterGroup[] monsterGroups = monsterEditor.getMonsterGroups();
        StringBuilder monsterGroupSb = new StringBuilder("unsigned char monster_group[")
                .append(monsterGroups.length * 0x0A)
                .append("] = {\n");
        for (int i = 0; i < monsterGroups.length; i++) {
            MonsterGroup monsterGroup = monsterGroups[i];
            byte[] monsterIds = monsterGroup.getMonsters();
            for (int monsterIdx = 0; monsterIdx < 0x0A; monsterIdx++) {
                // 特殊怪物组合
                int monsterId = monsterIds[monsterIdx];
                monsterGroupSb.append(monsterId).append(",");
            }
            monsterGroupSb.append("\n");
        }
        monsterGroupSb.append("};\n");

        StringBuilder stringBuilder = new StringBuilder("unsigned char monster_distribution_0[256] = {");
        for (int i = 0; i < worldMapRealms.size(); i++) {
            if (i % 10 == 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(worldMapRealms.get(i)).append(",");
        }
        stringBuilder.append("};\n");
        Log.d(TAG, stringBuilder.toString());

        StringBuilder smallMapSb = new StringBuilder("unsigned char monster_distribution_4_samll_maps")
                .append("[112]= {");
        for (int i = 128; i < 240; i++) {
            if (i % 10 == 0) {
                smallMapSb.append("\n");
            }
            MapProperties mapProperties = mapPropertiesEditor.getMapProperties(i);
            smallMapSb.append(mapProperties.monsterGroupIndex).append(",");
        }
        smallMapSb.append("};\n");
        Log.d(TAG, smallMapSb.toString());

        // 小地图怪物组合
        // ***不确定


//        for (byte specialMonsterGroupId : monsterGroup.getSpecialMonsterGroups()) {
//            SpecialMonsterGroup specialMonsterGroup = monsterEditor.getSpecialMonsterGroup(specialMonsterGroupId & 0xFF);
//            for (int i = 0; i < specialMonsterGroup.monsters.length; i++) {
//                // 这个特殊怪物的数量
//                int count = specialMonsterGroup.counts[i];
//                // 怪物
//                int monsterId = specialMonsterGroup.monsters[i];
//
//            }
//
//        }
    }

    private void generateMapProperties(MetalMaxRe metalMaxRe) {
        IMapPropertiesEditor mapPropertiesEditor = metalMaxRe.getEditorManager().getEditor(IMapPropertiesEditor.class);
        Map<Integer, MapProperties> mapPropertiesMap = mapPropertiesEditor.getMapProperties();
        StringBuilder musicStringBuilder = new StringBuilder("unsigned byte music[MAP_COUNT] = {\n");
        StringBuilder movableStringBuilder = new StringBuilder("unsigned byte movable_size[MAP_COUNT*2] = {\n");
        StringBuilder movableOffsetStringBuilder = new StringBuilder("unsigned byte movable_offset[MAP_COUNT*2] = {\n");
        StringBuilder headStringBuilder = new StringBuilder("unsigned byte head[MAP_COUNT] = {\n");

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

            if (mapId % 10 == 0) {
                musicStringBuilder.append("\n");
                movableStringBuilder.append("\n");
                movableOffsetStringBuilder.append("\n");
                headStringBuilder.append("\n");
            }

            musicStringBuilder.append(entry.getValue().getMusic()).append(",");
            movableStringBuilder.append(entry.getValue().movableWidth).append(",")
                    .append(entry.getValue().movableHeight).append(",");
            movableOffsetStringBuilder.append(entry.getValue().movableWidthOffset).append(",")
                    .append(entry.getValue().movableHeightOffset).append(",");
            headStringBuilder.append(entry.getValue().getHead()).append(",");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(musicStringBuilder.toString()).append("};\n");
        stringBuilder.append(movableStringBuilder.toString()).append("};\n");
        stringBuilder.append(movableOffsetStringBuilder.toString()).append("};\n");
        stringBuilder.append(headStringBuilder.toString()).append("};\n");
        FileUtils.writeFile("map/map_data.cpp", stringBuilder.toString());
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
