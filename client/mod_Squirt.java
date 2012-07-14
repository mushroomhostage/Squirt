
import forge.*;

class BlockExample extends pb/*Block*/ {
    protected BlockExample(int i, int j, acn/*Material*/ material) {
        super(i, j, material);
    }
}

public class mod_Squirt extends NetworkMod {
    public mod_Squirt() {
        pb/*Block*/ block = new BlockExample(255, 0, acn.e/*Material.something*/).c(3F).b(10F).a("Example");
        ModLoader.registerBlock(block);
        ModLoader.addName(block, "Example Block from Squirt");
    }

    public void load() {
        System.out.println("loading mod_Squirt!");
    }

    public String getVersion() {
        return "0";
    }

    public boolean clientSideRequired() {
        return false;
    }

    public boolean serverSideRequired() {
        return false;
    }
}

