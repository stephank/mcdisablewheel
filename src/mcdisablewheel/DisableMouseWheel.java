package mcdisablewheel;

import static javassist.bytecode.Opcode.INVOKESTATIC;
import static javassist.bytecode.Opcode.ICONST_0;

import com.prupe.mcpatcher.BytecodePatch;
import com.prupe.mcpatcher.BytecodeSignature;
import com.prupe.mcpatcher.ClassMod;
import com.prupe.mcpatcher.MethodRef;
import com.prupe.mcpatcher.Mod;

public final class DisableMouseWheel extends Mod {
    public DisableMouseWheel() {
        name = "Disable Mouse Wheel";
        author = "stephan@kochen.nl";
        description = "Disables the mouse wheel for inventory scrolling";
        version = "0.3";

        addClassMod(new MinecraftMod());
    }

    private final class MinecraftMod extends ClassMod {
        public MinecraftMod() {
            // Find the main tick handling method.
            MethodRef runTick = new MethodRef(getDeobfClass(), "runTick", "()V");
            addClassSignature(new BytecodeSignature() {
                @Override
                public String getMatchExpression() {
                    return buildExpression(
                        push("animateTick")
                    );
                }
            }.setMethod(runTick));

            // Patch the mouse event handling.
            addPatch(new BytecodePatch() {
                @Override
                public String getDescription() {
                    return "disable mouse wheel";
                }

                @Override
                public String getMatchExpression() {
                    // Find the call to Mouse.getEventDWheel().
                    return buildExpression(
                        reference(INVOKESTATIC, new MethodRef("org/lwjgl/input/Mouse", "getEventDWheel", "()I"))
                    );
                }

                @Override
                public byte[] getReplacementBytes() {
                    // Simply replace it by 0.
                    return buildCode(
                        ICONST_0
                    );
                }
            }.targetMethod(runTick));
        }
    }
}
