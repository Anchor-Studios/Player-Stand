package com.anchorstudios.playerstand.entity;

import com.anchorstudios.playerstand.Config;
import com.anchorstudios.playerstand.PlayerStand;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class PlayerStandEntity extends Mob {

    private static final EntityDataAccessor<String> PLAYER_NAME_DATA =
            SynchedEntityData.defineId(PlayerStandEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Byte> DATA_CLIENT_FLAGS =
            SynchedEntityData.defineId(PlayerStandEntity.class, EntityDataSerializers.BYTE);

    private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
    private boolean invisible;

    // Define the priority order for equipment slots
    private static final List<EquipmentSlot> EQUIPMENT_ORDER = Arrays.asList(
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND
    );

    public PlayerStandEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PLAYER_NAME_DATA, "NOT PLAYER");
        this.entityData.define(DATA_CLIENT_FLAGS, (byte)0);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected Vec3 getLeashOffset() {
        return new Vec3(0.0D, 0.75D * this.getEyeHeight(), this.getBbWidth() * 0.4D);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return !this.isInvisible();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.kill();
            return true;
        }
        if (!this.level().isClientSide && !this.isRemoved()) {
            boolean instantBreak = source.getEntity() instanceof Player && ((Player)source.getEntity()).getAbilities().instabuild;
            if (instantBreak) {
                this.playBrokenSound();
                this.discard();
                return true;
            }

            // Apply damage
            this.setHealth(this.getHealth() - 3.0F);

            if (this.getHealth() <= 0) {
                this.playBrokenSound();

                // Drop all equipment (armor + hands)
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack itemstack = this.getItemBySlot(slot);
                    if (!itemstack.isEmpty()) {
                        this.spawnAtLocation(itemstack);
                        this.setItemSlot(slot, ItemStack.EMPTY);
                    }
                }

                // Drop the stand item itself
                this.spawnAtLocation(PlayerStand.PLAYER_STAND_ITEM.get());
                this.discard();
            }
            return true;
        }
        return false;
    }

    private void playBrokenSound() {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        // Always return SUCCESS to prevent vanilla armor equipping
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // Handle player binding with shift-right click
        if (player.isShiftKeyDown() && heldItem.isEmpty() && Config.ALLOW_PLAYER_BINDING.get()) {
            boolean alreadyHasTexture = !"NOT PLAYER".equals(this.getPlayerName());
            boolean canRetexture = !alreadyHasTexture || Config.ALLOW_RETEXTURE_EXISTING.get();

            if (canRetexture) {
                this.setPlayerName(player.getName().getString());
                String displayName = player.getDisplayName().getString() + "'s Player Stand";
                this.setCustomName(Component.literal(displayName));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        // Handle normal right-click interactions
        if (!player.isShiftKeyDown()) {
            if (!heldItem.isEmpty()) {
                // Only accept single items
                if (heldItem.getCount() > 1) {
                    return InteractionResult.SUCCESS; // Cancel vanilla but don't process
                }

                ItemStack singleItem = heldItem.copy();
                singleItem.setCount(1);

                // Get all valid slots for this item
                List<EquipmentSlot> validSlots = getValidSlotsForItem(singleItem);

                // Try to find first empty valid slot
                for (EquipmentSlot slot : validSlots) {
                    if (this.getItemBySlot(slot).isEmpty()) {
                        this.setItemSlot(slot, singleItem);
                        player.setItemInHand(hand, heldItem.copyWithCount(heldItem.getCount() - 1));
                        return InteractionResult.SUCCESS;
                    }
                }
                return InteractionResult.SUCCESS; // Cancel vanilla even if no slot available
            } else {
                // Try to take items in reverse order
                for (int i = EQUIPMENT_ORDER.size() - 1; i >= 0; i--) {
                    EquipmentSlot slot = EQUIPMENT_ORDER.get(i);
                    ItemStack slotItem = this.getItemBySlot(slot);
                    if (!slotItem.isEmpty()) {
                        // Give exactly 1 item back
                        ItemStack singleItem = slotItem.copy();
                        singleItem.setCount(1);
                        player.setItemInHand(hand, singleItem);

                        // Remove 1 from the stack
                        slotItem.shrink(1);
                        if (slotItem.isEmpty()) {
                            this.setItemSlot(slot, ItemStack.EMPTY);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
                return InteractionResult.SUCCESS; // Cancel vanilla even if no item taken
            }
        }

        return InteractionResult.SUCCESS; // Always cancel vanilla interaction
    }

    private List<EquipmentSlot> getValidSlotsForItem(ItemStack stack) {
        EquipmentSlot naturalSlot = Mob.getEquipmentSlotForItem(stack);

        // If it's armor, prioritize its natural slot first, then hands
        if (naturalSlot.getType() == EquipmentSlot.Type.ARMOR) {
            return Arrays.asList(
                    naturalSlot,
                    EquipmentSlot.MAINHAND,
                    EquipmentSlot.OFFHAND
            );
        }

        // For non-armor items, just use hands
        return Arrays.asList(
                EquipmentSlot.MAINHAND,
                EquipmentSlot.OFFHAND
        );
    }


    public void setPlayerName(String name) {
        this.getEntityData().set(PLAYER_NAME_DATA, name);
    }

    public String getPlayerName() {
        return this.getEntityData().get(PLAYER_NAME_DATA);
    }

    @Override
    public void tick() {
        super.tick();
        this.hurtTime = 0; // reset every tick to prevent red flash
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("PlayerStandPlayerName")) {
            setPlayerName(tag.getString("PlayerStandPlayerName"));
        }

        if (tag.contains("HandItems", 9)) {
            ListTag handItemsTag = tag.getList("HandItems", 10);
            for (int i = 0; i < handItemsTag.size(); i++) {
                this.handItems.set(i, ItemStack.of(handItemsTag.getCompound(i)));
            }
        }

        if (tag.contains("ArmorItems", 9)) {
            ListTag armorItemsTag = tag.getList("ArmorItems", 10);
            for (int i = 0; i < armorItemsTag.size(); i++) {
                this.armorItems.set(i, ItemStack.of(armorItemsTag.getCompound(i)));
            }
        }

        this.setInvisible(tag.getBoolean("Invisible"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("PlayerStandPlayerName", getPlayerName());

        ListTag handItemsTag = new ListTag();
        for (ItemStack item : this.handItems) {
            handItemsTag.add(item.save(new CompoundTag()));
        }
        tag.put("HandItems", handItemsTag);

        ListTag armorItemsTag = new ListTag();
        for (ItemStack item : this.armorItems) {
            armorItemsTag.add(item.save(new CompoundTag()));
        }
        tag.put("ArmorItems", armorItemsTag);

        tag.putBoolean("Invisible", this.isInvisible());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        switch(slot.getType()) {
            case HAND:
                return this.handItems.get(slot.getIndex());
            case ARMOR:
                return this.armorItems.get(slot.getIndex());
            default:
                return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        this.verifyEquippedItem(stack);
        switch(slot.getType()) {
            case HAND:
                this.handItems.set(slot.getIndex(), stack);
                break;
            case ARMOR:
                this.armorItems.set(slot.getIndex(), stack);
                this.setDropChance(slot, 2.0F);
                break;
        }
    }

    @Override
    public boolean canTakeItem(ItemStack stack) {
        EquipmentSlot slot = getEquipmentSlotForItem(stack);
        return this.getItemBySlot(slot).isEmpty() && this.isAlive();
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
        this.setSharedFlag(5, invisible);
    }

    protected void setSharedFlag(int flag, boolean value) {
        byte b = this.entityData.get(DATA_CLIENT_FLAGS);
        if (value) {
            b = (byte)(b | flag);
        } else {
            b = (byte)(b & ~flag);
        }
        this.entityData.set(DATA_CLIENT_FLAGS, b);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 9.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }
}