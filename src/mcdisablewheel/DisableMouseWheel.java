package mcdisablewheel;

import static javassist.bytecode.Opcode.INVOKESTATIC;
import static javassist.bytecode.Opcode.ICONST_0;
import java.io.IOException;

import com.pclewis.mcpatcher.BytecodePatch;
import com.pclewis.mcpatcher.BytecodeSignature;
import com.pclewis.mcpatcher.ClassMod;
import com.pclewis.mcpatcher.MethodRef;
import com.pclewis.mcpatcher.Mod;

public final class DisableMouseWheel extends Mod {
    public DisableMouseWheel() {
        name = "Disable Mouse Wheel";
        author = "stephan@kochen.nl";
        description = "Disables the mouse wheel for inventory scrolling";
        version = "0.1";

        classMods.add(new MinecraftMod());
    }

    private final class MinecraftMod extends ClassMod {
        public MinecraftMod() {
            // Find the main tick handling method.
            MethodRef runTick = new MethodRef(getDeobfClass(), "runTick", "()V");
            classSignatures.add(new BytecodeSignature() {
                @Override
                public String getMatchExpression() {
                    return buildExpression(
                        push("animateTick")
                    );
                }
            }.setMethod(runTick));

            // Patch the mouse event handling.
            patches.add(new BytecodePatch() {
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
                public byte[] getReplacementBytes() throws IOException {
                    // Simply replace it by 0.
                    return buildCode(
                        ICONST_0
                    );
                }
            }.targetMethod(runTick));
        }
    }
}
