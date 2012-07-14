package me.exphc.Squirt;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.Material.*;
import org.bukkit.material.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import org.bukkit.inventory.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import org.bukkit.scheduler.*;
import org.bukkit.enchantments.*;
import org.bukkit.*;

import java.util.*;
import java.util.logging.*;

public class Squirt extends JavaPlugin implements Listener {
    Logger log = Logger.getLogger("Minecraft");

    public void onEnable() {
        log.info("enabling");
    }

    public void onDisable() {
    }
}
