package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.Patch;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class LanguageListPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "elementClicked", "func_148144_a");
		MethodInsnNode refreshResources = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				refreshResources = (MethodInsnNode) instruction;

				if("refreshResources".equals(refreshResources.name)) {
					break;
				}

				refreshResources = null;
			}
		}

		final MethodInsnNode callReloadLanguage = new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(LanguageListPatch.class),
				"reloadLanguage",
				"()V",
				false
		);

		method.instructions.insert(refreshResources, callReloadLanguage);

		final AbstractInsnNode previous = refreshResources.getPrevious();

		method.instructions.remove(previous.getPrevious());
		method.instructions.remove(previous);
		method.instructions.remove(refreshResources);

		return true;
	}

	public static void reloadLanguage() {
		final Minecraft mc = Minecraft.getInstance();
		mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
	}
}
