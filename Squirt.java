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
import java.lang.reflect.*;

public class Squirt extends JavaPlugin implements Listener {
    Logger log = Logger.getLogger("Minecraft");

    public void onEnable() {
        log.info("enabling");

        addMaterial("X255", 255);

    }

    public void onDisable() {
    }

    @SuppressWarnings("unchecked")
    public void addMaterial(String name, int id) {
        // Add to enum itself
        Material material = addEnum(Material.class, name, new Class[] { int.class }, new Object[] { id });
        log.info("added material " + material);

        // Add to lookup hash for Material.getMaterial(String)
        try {
            Field field = Material.class.getDeclaredField("BY_NAME");
            field.setAccessible(true);
            Object object = field.get(null);
            Map<String, Material> BY_NAME = (Map<String, Material>)object;
            BY_NAME.put(name, material);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("getMaterial = " + Material.getMaterial("X255"));

        try {
            Field field = Material.class.getDeclaredField("byId");
            field.setAccessible(true);
            Object object = field.get(null);
            Material[] byId = (Material[])object;
            byId[id] = material;
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("getMaterial = " + Material.getMaterial(255));
    }

    /* magic to add new fields to enums - thanks to 
    https://github.com/MinecraftPortCentral/Bukkit/blob/mcportcentral/src/main/java/org/bukkit/Material.java#L516
   	*
	 * Everything below this is found at the site below, and updated to be able to compile in Eclipse/Java 1.6+
     * Also modified for use in decompiled code.
	 * Found at: http://niceideas.ch/roller2/badtrash/entry/java_create_enum_instances_dynamically
	 */
	private static Object reflectionFactory      = null;
	private static Method newConstructorAccessor = null;
	private static Method newInstance            = null;
	private static Method newFieldAccessor       = null;
	private static Method fieldAccessorSet       = null;
	private static boolean isSetup               = false;

	private static void setup()
	{
		if (isSetup)
		{
			return;
		}
		try {
			Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory");
			reflectionFactory      = getReflectionFactory.invoke(null);
			newConstructorAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newConstructorAccessor", Constructor.class);
			newInstance            = Class.forName("sun.reflect.ConstructorAccessor").getDeclaredMethod("newInstance", Object[].class);
			newFieldAccessor       = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newFieldAccessor", Field.class, boolean.class);
			fieldAccessorSet       = Class.forName("sun.reflect.FieldAccessor").getDeclaredMethod("set", Object.class, Object.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		isSetup = true;
	}

	private static Object getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes) throws Exception {
		Class<?>[] parameterTypes = null;

		parameterTypes = new Class[additionalParameterTypes.length + 2];
		parameterTypes[0] = String.class;
		parameterTypes[1] = int.class;
		System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);

		return newConstructorAccessor.invoke(reflectionFactory, enumClass.getDeclaredConstructor(parameterTypes));
	}

	private static <T extends Enum<?>> T makeEnum(Class<T> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception {
		Object[] parms = null;

		parms = new Object[additionalValues.length + 2];
		parms[0] = value;
		parms[1] = Integer.valueOf(ordinal);
		System.arraycopy(additionalValues, 0, parms, 2, additionalValues.length);

		return enumClass.cast(newInstance.invoke(getConstructorAccessor(enumClass, additionalTypes), new Object[]{parms}));
	}

	private static void setFailsafeFieldValue(Field field, Object target, Object value) throws Exception {
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, field, false);
		fieldAccessorSet.invoke(fieldAccessor, target, value);
	}

	private static void blankField(Class<?> enumClass, String fieldName) throws Exception {
		for (Field field : Class.class.getDeclaredFields()) {
			if (field.getName().contains(fieldName)) {
				field.setAccessible(true);
				setFailsafeFieldValue(field, enumClass, null);
				break;
			}
		}
	}

	private static void cleanEnumCache(Class<?> enumClass) throws Exception {
		blankField(enumClass, "enumConstantDirectory");
		blankField(enumClass, "enumConstants");
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName, Class<?>[] paramTypes, Object[] paramValues) {
		if (!isSetup) setup();
		Field valuesField = null;
		Field[] fields = enumType.getDeclaredFields();
		int flags = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL | 0x1000 /*SYNTHETIC*/;
		String valueType = String.format("[L%s;", enumType.getName()/*.replace('.', '/')*/);

		for (Field field : fields) {
			if ((field.getModifiers() & flags) == flags &&
				field.getType().getName().equals(valueType))
			{
				valuesField = field;
				break;
			}
		}
		valuesField.setAccessible(true);

		try {
			T[] previousValues = (T[])valuesField.get(enumType);
			List<T> values = new ArrayList<T>(Arrays.asList(previousValues));
			T newValue = (T)makeEnum(enumType, enumName, values.size(), paramTypes, paramValues);
			values.add(newValue);
			setFailsafeFieldValue(valuesField, null, values.toArray((T[]) Array.newInstance(enumType, 0)));
			cleanEnumCache(enumType);

			return newValue;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
