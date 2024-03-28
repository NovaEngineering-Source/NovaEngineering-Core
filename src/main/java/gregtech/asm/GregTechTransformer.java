package gregtech.asm;

import gregtech.asm.util.TargetClassVisitor;
import gregtech.asm.visitors.AbstractCTMBakedModelVisitor;
import gregtech.asm.visitors.LittleTilesVisitor;
import gregtech.asm.visitors.ModelCTMVisitor;
import gregtech.asm.visitors.RegionRenderCacheBuilderVisitor;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class GregTechTransformer implements IClassTransformer, Opcodes {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        String internalName = transformedName.replace('.', '/');
        switch (internalName) {
            case RegionRenderCacheBuilderVisitor.TARGET_CLASS_NAME: {
                ClassReader classReader = new ClassReader(basicClass);
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                classReader.accept(new TargetClassVisitor(classWriter, RegionRenderCacheBuilderVisitor.TARGET_METHOD,
                        RegionRenderCacheBuilderVisitor::new), 0);
                return classWriter.toByteArray();
            }
//            case EntityRendererVisitor.TARGET_CLASS_NAME: {
//                ClassReader classReader = new ClassReader(basicClass);
//                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
//                classReader.accept(new TargetClassVisitor(classWriter, EntityRendererVisitor.TARGET_METHOD,
//                        EntityRendererVisitor::new), 0);
//                return classWriter.toByteArray();
//            }
            case ModelCTMVisitor.TARGET_CLASS_NAME: {
                ClassReader classReader = new ClassReader(basicClass);
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                classReader.accept(
                        new TargetClassVisitor(classWriter, ModelCTMVisitor.TARGET_METHOD, ModelCTMVisitor::new), 0);
                return classWriter.toByteArray();
            }
            case AbstractCTMBakedModelVisitor.TARGET_CLASS_NAME: {
                ClassReader classReader = new ClassReader(basicClass);
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                classReader.accept(new TargetClassVisitor(classWriter, AbstractCTMBakedModelVisitor.TARGET_METHOD,
                        AbstractCTMBakedModelVisitor::new), 0);
                return classWriter.toByteArray();
            }
            case LittleTilesVisitor.TARGET_CLASS_NAME: {
                ClassReader classReader = new ClassReader(basicClass);
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                classReader.accept(
                        new TargetClassVisitor(classWriter, LittleTilesVisitor.TARGET_METHOD, LittleTilesVisitor::new),
                        0);
                return classWriter.toByteArray();
            }
        }
        return basicClass;
    }
}
