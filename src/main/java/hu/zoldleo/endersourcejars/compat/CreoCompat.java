package hu.zoldleo.endersourcejars.compat;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import com.hollingsworth.ars_creo.api.SourceInfo;
import hu.zoldleo.endersourcejars.EnderSourceJars;
import hu.zoldleo.endersourcejars.storage.EnderSourceStorage;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class CreoCompat {
    public static void registerSourceInfo() {
        SourceInfo.register(EnderSourceJars.ENDER_SOURCE_JAR_BLOCK.get(), EnderSourceInfo::new);
    }

    public static class EnderSourceInfo extends SourceInfo {
        protected final Frequency frequency;

        @SuppressWarnings("all")
        public EnderSourceInfo(StructureTemplate.StructureBlockInfo blockInfo) {
            super(blockInfo);
            frequency = new Frequency(blockInfo.nbt().getCompound("Frequency"));
        }

        @Override
        public int getSource() {
            return EnderStorageManager.instance(false).getStorage(frequency, EnderSourceStorage.TYPE).getStorage().getSource();
        }

        @Override
        public int getMaxSource() {
            return 10_000;
        }

        @Override
        public int getTransferRate() {
            return 10_000;
        }

        @Override
        public void removeSource(int amount) {
            EnderStorageManager.instance(false).getStorage(frequency, EnderSourceStorage.TYPE).getStorage().extractSource(amount, false);
        }

        @Override
        public void addSource(int amount) {
            EnderStorageManager.instance(false).getStorage(frequency, EnderSourceStorage.TYPE).getStorage().receiveSource(amount, false);
        }

        @Override
        protected StructureTemplate.StructureBlockInfo removeWithUpdate(Level level, int amount) {
            removeSource(amount);
            return null;
        }

        @Override
        protected StructureTemplate.StructureBlockInfo addWithUpdate(Level level, int amount) {
            addSource(amount);
            return null;
        }
    }
}