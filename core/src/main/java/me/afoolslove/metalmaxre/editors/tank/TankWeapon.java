package me.afoolslove.metalmaxre.editors.tank;

import me.afoolslove.metalmaxre.AttackRange;
import me.afoolslove.metalmaxre.editors.data.IDataValueEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * 坦克装备
 * 主炮、副炮、S-E
 *
 * @author AFoolLove
 */
public class TankWeapon extends TankEquipmentItem {
    /**
     * 攻击动画
     */
    public byte attackAnim;

    /**
     * 可装备的穴
     */
    public byte canEquipped;

    /**
     * 设置攻击动画
     */
    public void setAttackAnim(@Range(from = 0x00, to = 0xFF) int attackAnim) {
        this.attackAnim = (byte) (attackAnim & 0xFF);
    }

    /**
     * 设置攻击力
     *
     * @see IDataValueEditor#get2ByteValues()
     */
    public void setAttack(@Range(from = 0x00, to = 0xFF) int attack) {
        setAttack((byte) (attack & 0xFF));
    }

    /**
     * 设置攻击力
     *
     * @see IDataValueEditor#get2ByteValues()
     */
    public void setAttack(byte attack) {
        value = attack;
    }

    /**
     * 设置攻击的范围
     */
    public void setAttackRange(@NotNull AttackRange range) {
        // 清除当前攻击范围数据
        canEquipped &= 0B1110_0111;
        // 设置攻击范围
        canEquipped |= range.getValue();
    }

    /**
     * 设置改装备的炮弹容量
     */
    public void setShellCapacity(@NotNull TankShellCapacity capacity) {
        // 清除之前的容量
        canEquipped &= 0B1111_1000;
        // 设置容量
        canEquipped |= capacity.getValue();
    }

    /**
     * 设置可装备此装备的穴
     */
    public void setCanEquipped(@Range(from = 0x00, to = 0xFF) int canEquipped) {
        this.canEquipped = (byte) (canEquipped & 0xFF);
    }

    /**
     * 设置可装备此装备的穴
     */
    public void setCanEquipped(@NotNull TankWeaponSlot... tankWeaponSlots) {
        canEquipped &= 0B0001_1111;
        if (tankWeaponSlots.length == 0) {
            // 谁也不能装备
            // 为空可还行
            return;
        }
        for (TankWeaponSlot tankWeaponSlot : tankWeaponSlots) {
            switch (tankWeaponSlot) {
                case MAIN_GUN -> canEquipped |= (byte) 0B1000_0000;
                case SECONDARY_GUN -> canEquipped |= (byte) 0B0100_0000;
                case SPECIAL_EQUIPMENT -> canEquipped |= (byte) 0B0010_0000;
            }
        }
    }

    /**
     * 添加可装备此装备的穴
     */
    public void addCanEquipped(@NotNull TankWeaponSlot... tankWeaponSlots) {
        for (TankWeaponSlot tankWeaponSlot : tankWeaponSlots) {
            switch (tankWeaponSlot) {
                case MAIN_GUN -> canEquipped |= (byte) 0B1000_0000;
                case SECONDARY_GUN -> canEquipped |= (byte) 0B0100_0000;
                case SPECIAL_EQUIPMENT -> canEquipped |= (byte) 0B0010_0000;
            }
        }
    }

    /**
     * 移除可装备此装备的穴
     */
    public void removeCanEquipped(@NotNull TankWeaponSlot... tankWeaponSlots) {
        for (TankWeaponSlot tankWeaponSlot : tankWeaponSlots) {
            switch (tankWeaponSlot) {
                case MAIN_GUN -> canEquipped |= (byte) 0B0111_1111;
                case SECONDARY_GUN -> canEquipped |= (byte) 0B1011_1111;
                case SPECIAL_EQUIPMENT -> canEquipped |= (byte) 0B1101_1111;
            }
        }
    }

    /**
     * @return 是否可以装备到某个穴上
     */
    public boolean hasEquipped(@NotNull TankWeaponSlot slot) {
        switch (slot) {
            case MAIN_GUN:
                return (canEquipped & 0B1000_0000) != 0;
            case SECONDARY_GUN:
                return (canEquipped & 0B0100_0000) != 0;
            case SPECIAL_EQUIPMENT:
                return (canEquipped & 0B0010_0000) != 0;
            default:
                break;
        }
        return false;
    }

    /**
     * @return 攻击动画
     */
    public byte getAttackAnim() {
        return attackAnim;
    }

    /**
     * @return 攻击动画
     */
    public int intAttackAnim() {
        return getAttackAnim() & 0xFF;
    }

    /**
     * @return 攻击范围
     */
    public AttackRange getAttackRange() {
        return AttackRange.fromValue(canEquipped & 0B0001_1000);
    }

    /**
     * @return 该武器的攻击范围和可装备的穴
     */
    public byte getCanEquipped() {
        return canEquipped;
    }

    /**
     * @return 攻击力
     * @see IDataValueEditor#get2ByteValues()
     */
    public byte getAttack() {
        return value;
    }

    /**
     * @return 攻击力
     * @see IDataValueEditor#get2ByteValues()
     */
    public int intAttack() {
        return getAttack() & 0xFF;
    }

    /**
     * @return 指向的真实攻击力值
     * @see IDataValueEditor#get2ByteValues()
     */
    public int getAttackValue(@NotNull IDataValueEditor dataValues) {
        return dataValues.getValues().get(value & 0xFF).intValue();
    }

    /**
     * @return 炮弹容量
     */
    public TankShellCapacity getShellCapacity() {
        return TankShellCapacity.fromValue(canEquipped);
    }
}
