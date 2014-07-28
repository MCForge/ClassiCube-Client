package com.mojang.minecraft.level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.mojang.util.LogUtil;
import com.mojang.minecraft.Minecraft;
import com.mojang.nbt.CompressedStreamTools;
import com.mojang.nbt.NBTTagCompound;

public class LevelSerializer {

    Level level;
    String EXT = ".cw";

    public LevelSerializer(Level level) {
        this.level = level;
    }

    private static byte[] asByteArray(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }
        return buffer;
    }

    void save(File fullFilePath) throws FileNotFoundException, IOException, Exception {
        LogUtil.logInfo("Saving level " + fullFilePath.getAbsolutePath());
        if (level == null) {
            // TODO Don't throw generic exception
            throw new Exception("level");
        }

        NBTTagCompound master = new NBTTagCompound("ClassicWorld");
        master.setByte("FormatVersion", (byte) 1);
        master.setString("Name", "SinglePlayerMap");
        master.setByteArray("UUID", asByteArray(UUID.randomUUID()));
        master.setShort("X", (short) level.width);
        master.setShort("Y", (short) level.height);
        master.setShort("Z", (short) level.length);
        master.setByteArray("BlockArray", level.blocks);

        NBTTagCompound createdBy = new NBTTagCompound("CreatedBy");
        createdBy.setString("Service", "ClassiCube");
        createdBy.setString("Username", "ClassiCube User");
        master.setCompoundTag("CreatedBy", createdBy);

        NBTTagCompound spawn = new NBTTagCompound("Spawn");
        spawn.setShort("X", (short) level.player.x);
        spawn.setShort("Y", (short) level.player.y);
        spawn.setShort("Z", (short) level.player.z);
        spawn.setByte("H", (byte) level.player.xRot);
        spawn.setByte("P", (byte) level.player.yRot);
        master.setCompoundTag("Spawn", spawn);
        
        // Metadata tag is required by ClassicWorld specs, even if empty.
        master.setCompoundTag("Metadata", new NBTTagCompound("Metadata"));

        String fileName = fullFilePath
                + (fullFilePath.getAbsolutePath().endsWith(EXT) ? "" : EXT);
        try (FileOutputStream fs = new FileOutputStream(new File(fileName))) {
            CompressedStreamTools.writeCompressed(master, fs);
        }
    }

    public void saveMap(File file) throws FileNotFoundException, IOException, Exception {
        save(file);
    }

    public void saveMap(String Name) throws FileNotFoundException, IOException, Exception {
        save(new File(Minecraft.getMinecraftDirectory(), Name));
    }
}
