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
}
