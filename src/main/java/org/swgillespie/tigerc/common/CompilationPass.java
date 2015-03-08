package org.swgillespie.tigerc.common;

/**
 * Created by sean on 3/2/15.
 */
public interface CompilationPass<TInput, TOutput> {
    String getName();
    TOutput runPass(CompilationSession session, TInput input);
}
