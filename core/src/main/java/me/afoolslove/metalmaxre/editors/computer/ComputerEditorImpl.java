package me.afoolslove.metalmaxre.editors.computer;

import me.afoolslove.metalmaxre.MetalMaxRe;
import me.afoolslove.metalmaxre.editors.AbstractEditor;
import me.afoolslove.metalmaxre.editors.Editor;
import me.afoolslove.metalmaxre.event.editors.computer.EditorComputerEvent;
import me.afoolslove.metalmaxre.utils.DataAddress;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 计算机编辑器
 * <p>
 * <p>
 * 计算机一共有4个属性，分别为：所在地图(map)、类型(type)、坐标(x,y)
 * <p>
 * 使用 (MAP*N)+(TYPE*N)+(X*N)+(Y*N) 的格式储存
 * <p>
 * e.g:
 * <p>
 * 物品A(map,type,x,y): 1,0,1,1<p>
 * 物品B(map,type,x,y): 2,0,2,2<p>
 * 物品N......<p>
 * <p>
 * 以上数据为：(12)(00)(12)(12)<p>
 *
 * @author AFoolLove
 */
public class ComputerEditorImpl extends AbstractEditor implements IComputerEditor<Computer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerEditorImpl.class);
    protected final DataAddress computerAddress;
    private final List<Computer> computers = new ArrayList<>(getMaxCount());

    public ComputerEditorImpl(@NotNull MetalMaxRe metalMaxRe) {
        super(metalMaxRe);
        // 通用地址，兼容已知的版本
        this.computerAddress = DataAddress.fromPRG(0x39DD2 - 0x10, 0x39FBD - 0x10);
    }

    public ComputerEditorImpl(@NotNull MetalMaxRe metalMaxRe, DataAddress dataAddress) {
        super(metalMaxRe);
        this.computerAddress = dataAddress;
    }

    @Editor.Load
    public void onLoad() {
        // 初始化计算机
        getComputers().clear();

        // data[0] = map
        // data[1] = type
        // data[2] = x
        // data[3] = y
        byte[][] data = new byte[4][getMaxCount()];
        getBuffer().getAABytes(getComputerAddress(), 0, getMaxCount(), data);

        for (int i = 0; i < getMaxCount(); i++) {
            getComputers().add(new Computer(data[0][i], data[1][i], data[2][i], data[3][i]));
        }

    }

    @Editor.Apply
    public void onApply() {
        List<Computer> computers = getComputers();
        int count = Math.min(getMaxCount(), computers.size());

        // data[0] = map
        // data[1] = type
        // data[2] = x
        // data[3] = y
        byte[][] data = new byte[4][getMaxCount()];

        for (int i = 0; i < count; i++) {
            Computer computer = computers.get(i);
            data[0][i] = computer.getMap();
            data[1][i] = computer.getType();
            data[2][i] = computer.getX();
            data[3][i] = computer.getY();
        }

        // 如果有空的计算机，将空的计算机设置到地图 0xFF 中去
        int remain = getMaxCount() - count;
        if (remain > 0) {
            Arrays.fill(data[0], count, getMaxCount(), (byte) 0xFF);
        }
        // 写入计算机
        getBuffer().put(getComputerAddress(), data);

        if (computers.size() > count) {
            LOGGER.error("计算机编辑器：计算机未写入{}个", computers.size() - count);
            for (Computer computer : computers.subList(count, computers.size())) {
                LOGGER.error("计算机编辑器：计算机未写入 {}", computer);
            }
        } else if (computers.size() < count) {
            LOGGER.info("计算机编辑器：剩余{}个计算机空闲空间", count - computers.size());
        }
    }

    @Override
    public DataAddress getComputerAddress() {
        return computerAddress;
    }

    @Override
    public List<Computer> getComputers() {
        return computers;
    }

    @Override
    public void addComputer(@NotNull Computer computer) {
        computers.add(computer);
    }

    @Override
    public void removeComputer(@NotNull Computer computer) {
        computers.remove(computer);
        getMetalMaxRe().getEventHandler().callEvent(new EditorComputerEvent.RemoveComputer(getMetalMaxRe(), this, computer));
    }

    @Override
    public boolean replaceComputer(@NotNull Computer source, @NotNull Computer replace) {
        if (computers.contains(replace)) {
            return true;
        }
        if (computers.remove(source)) {
            // 移除旧计算机成功，添加新的计算机
            try {
                return computers.add(replace);
            } finally {
                getMetalMaxRe().getEventHandler().callEvent(new EditorComputerEvent.ReplaceComputer(getMetalMaxRe(), this, source, replace));
            }
        }
        return false;

    }
}
