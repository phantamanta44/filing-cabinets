package xyz.phanta.filcabref.block.filing_cabinet;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.item.L9ItemBlock;
import io.github.phantamanta44.libnine.util.collection.Accrue;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.libnine.util.world.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import xyz.phanta.filcabref.FilingCabinetsMod;
import xyz.phanta.filcabref.constant.LangConst;
import xyz.phanta.filcabref.init.FilingCabinetsBlocks;
import xyz.phanta.filcabref.init.FilingCabinetsGuis;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BlockFilingCabinet extends L9BlockStated {

    public static final IProperty<Type> TYPE = PropertyEnum.create("type", Type.class);
    public static final IProperty<EnumFacing> ROTATION
            = PropertyEnum.create("rotation", EnumFacing.class, EnumFacing.HORIZONTALS);

    public BlockFilingCabinet() {
        super(LangConst.BLOCK_FILING_CABINET, Material.IRON);
        setHardness(2F);
        setTileFactory((w, m) -> getStateFromMeta(m).getValue(TYPE).createTileEntity());
    }

    @Override
    protected void accrueProperties(Accrue<IProperty<?>> props) {
        props.acceptAll(TYPE, ROTATION);
    }

    @Override
    protected ItemBlockFilingCabinet initItemBlock() {
        return new ItemBlockFilingCabinet(this);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (Type type : Type.VALUES) {
            items.add(new ItemStack(this, 1, type.getItemMeta()));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        switch (Type.getForItemMeta(stack.getMetadata())) {
            case BASIC:
                tooltip.add(TextFormatting.GRAY + I18n.format(LangConst.TT_FILING_CABINET_BASIC));
                break;
            case ADVANCED:
                tooltip.add(TextFormatting.GRAY + I18n.format(LangConst.TT_FILING_CABINET_ADVANCED));
                break;
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).getItemMeta();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer, EnumHand hand) {
        return getDefaultState()
                .withProperty(TYPE, Type.getForItemMeta(meta))
                .withProperty(ROTATION, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            FilingCabinetsMod.INSTANCE.getGuiHandler().openGui(
                    player, FilingCabinetsGuis.FILING_CABINET, new WorldBlockPos(world, pos));
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileFilingCabinet tile = getTileEntity(world, pos);
        if (tile != null) {
            tile.dropInventory(world, WorldUtils.getBlockCenter(pos));
        }
        super.breakBlock(world, pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(ROTATION, rot.rotate(state.getValue(ROTATION)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withProperty(ROTATION, mirror.mirror(state.getValue(ROTATION)));
    }

    public enum Type implements IStringSerializable {

        BASIC(LangConst.GUI_NAME_FILING_CABINET_BASIC, TileFilingCabinet.Basic::new),
        ADVANCED(LangConst.GUI_NAME_FILING_CABINET_ADVANCED, TileFilingCabinet.Advanced::new);

        private static final Type[] VALUES = values();

        public static Type getForItemMeta(int meta) {
            return VALUES[meta];
        }

        private final String guiNameKey;
        private final Supplier<TileEntity> tileFactory;

        Type(String guiNameKey, Supplier<TileEntity> tileFactory) {
            this.guiNameKey = guiNameKey;
            this.tileFactory = tileFactory;
        }

        TileEntity createTileEntity() {
            return tileFactory.get();
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public String getGuiNameKey() {
            return guiNameKey;
        }

        public int getItemMeta() {
            return ordinal();
        }

        public ItemStack newStack(int count) {
            return new ItemStack(FilingCabinetsBlocks.FILING_CABINET, count);
        }

    }

}
