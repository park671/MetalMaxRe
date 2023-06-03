package me.afoolslove.metalmaxre;

import me.afoolslove.metalmaxre.editors.map.tileset.TileSetEditorImpl;
import me.afoolslove.metalmaxre.editors.map.world.WorldMapEditorImpl;
import me.afoolslove.metalmaxre.editors.monster.MonsterModel;
import me.afoolslove.metalmaxre.editors.monster.MonsterModelImpl;
import me.afoolslove.metalmaxre.editors.palette.Color;
import me.afoolslove.metalmaxre.helper.MonsterModelHelper;
import me.afoolslove.metalmaxre.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        MultipleMetalMaxRe multipleMetalMaxRe = new MultipleMetalMaxRe();
        File file = new File("/Users/youngpark/MetalMaxRe/core/src/main/resources/roms/MetalMax_Pre.nes");
        try {
            MetalMaxRe metalMaxRe = multipleMetalMaxRe.create(RomVersion.getChinese(),
                    file.toPath(),
                    true);
            Future<?> future = metalMaxRe.getEditorManager().loadEditors();
            future.get();
            MonsterModelImpl monsterModel = metalMaxRe.getEditorManager().getEditor(MonsterModelImpl.class);
            generateMonster(metalMaxRe, monsterModel);

            WorldMapEditorImpl worldMapEditor = metalMaxRe.getEditorManager().getEditor(WorldMapEditorImpl.class);
            TileSetEditorImpl tileSetEditor = metalMaxRe.getEditorManager().getEditor(TileSetEditorImpl.class);
            System.out.println("compelete!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateMonster(MetalMaxRe metalMaxRe, MonsterModelImpl monsterModel) {
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

    static List<Integer> palette = new ArrayList<>();

    private static int addColor(int argb) {
        if (!palette.contains(argb)) {
            palette.add(argb);
        }
        return palette.indexOf(argb);
    }
}
