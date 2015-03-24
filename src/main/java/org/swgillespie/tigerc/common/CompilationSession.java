package org.swgillespie.tigerc.common;

import org.swgillespie.tigerc.backend.instructionselection.InstructionCache;
import org.swgillespie.tigerc.backend.regalloc.liveness.controlflow.ControlFlowCache;
import org.swgillespie.tigerc.canonicalize.controlflow.BasicBlockCache;
import org.swgillespie.tigerc.canonicalize.tracegeneration.ProcBodyCache;
import org.swgillespie.tigerc.semantic.TypeCache;
import org.swgillespie.tigerc.trans.StackFrameFactory;
import org.swgillespie.tigerc.trans.Target;
import org.swgillespie.tigerc.trans.TempFactory;
import org.swgillespie.tigerc.trans.mips.MipsStackFrameFactory;
import org.swgillespie.tigerc.trans.mips.MipsTempFactory;
import org.swgillespie.tigerc.trans.treebuild.IRTreeCache;
import org.swgillespie.tigerc.trans.escape.EscapeEntryCache;

/**
 * Created by sean on 3/2/15.
 */
public class CompilationSession {
    private DiagnosticSink diagnostics;
    private String currentFile;
    private SymbolPool symbolPool;
    private TypeCache typeCache;
    private EscapeEntryCache escapeEntryCache;
    private IRTreeCache irTreeCache;
    private Target target;
    private StackFrameFactory stackFrameFactory;
    private TempFactory tempFactory;
    private BasicBlockCache basicBlockCache;
    private ProcBodyCache procBodyCache;
    private InstructionCache instructionCache;
    private ControlFlowCache controlFlowCache;

    public CompilationSession(Target target) {
        this.diagnostics = null;
        this.currentFile = "<none>";
        this.symbolPool = new SymbolPool();
        this.typeCache = new TypeCache();
        this.escapeEntryCache = new EscapeEntryCache();
        this.irTreeCache = new IRTreeCache();
        this.target = target;
        this.basicBlockCache = new BasicBlockCache();
        this.procBodyCache = new ProcBodyCache();
        this.instructionCache = new InstructionCache();
        this.controlFlowCache = new ControlFlowCache();
        switch (target) {
            case MIPS:
                tempFactory = new MipsTempFactory();
                stackFrameFactory = new MipsStackFrameFactory(tempFactory);
                break;
            default:
                CompilerAssert.panic("unsupported target: " + target);
                break;
        }
    }

    public DiagnosticSink getDiagnosticSink() {
        return diagnostics;
    }

    public void setDiagnosticSink(DiagnosticSink diagnostics) {
        this.diagnostics = diagnostics;
    }

    public void addDiagnostic(Diagnostic d) {
        this.diagnostics.addDiagnostic(d);
    }

    public boolean hasAnyErrors() {
        return this.diagnostics.hasAnyErrors();
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
    }

    public TypeCache getTypeCache() {
        return typeCache;
    }

    public EscapeEntryCache getEscapeEntryCache() {
        return escapeEntryCache;
    }

    public IRTreeCache getIrTreeCache() {
        return irTreeCache;
    }

    public Symbol intern(String str) {
        return this.symbolPool.getSymbol(str);
    }

    public Target getTarget() {
        return target;
    }

    public StackFrameFactory getStackFrameFactory() {
        return stackFrameFactory;
    }

    public TempFactory getTempFactory() {
        return tempFactory;
    }

    public BasicBlockCache getBasicBlockCache() {
        return basicBlockCache;
    }

    public ProcBodyCache getProcBodyCache() {
        return procBodyCache;
    }

    public InstructionCache getInstructionCache() {
        return instructionCache;
    }

    public ControlFlowCache getControlFlowCache() {
        return controlFlowCache;
    }
}
