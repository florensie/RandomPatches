package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public final class WorldServerPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "<init>");

		TypeInsnNode createTeleporter = null;
		MethodInsnNode initTeleporter = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(createTeleporter == null) {
				if(instruction.getOpcode() == Opcodes.NEW) {
					createTeleporter = (TypeInsnNode) instruction;

					if(!"net/minecraft/world/Teleporter".equals(createTeleporter.desc)) {
						createTeleporter = null;
					}
				}

				continue;
			}

			if(instruction.getOpcode() == Opcodes.INVOKESPECIAL) {
				initTeleporter = (MethodInsnNode) instruction;

				if("net/minecraft/world/Teleporter".equals(initTeleporter.owner)) {
					break;
				}

				initTeleporter = null;
			}
		}

		//We can't use getName here because that requires loading RPTeleporter.class
		//which loads Teleporter.class which breaks Cubic Chunks and possibly other mods
		createTeleporter.desc = "com/therandomlabs/randompatches/common/RPTeleporter";
		initTeleporter.owner = "com/therandomlabs/randompatches/common/RPTeleporter";

		return true;
	}
}
