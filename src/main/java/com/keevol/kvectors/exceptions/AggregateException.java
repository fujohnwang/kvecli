package com.keevol.kvectors.exceptions;

/**
 * <pre>
 * ██╗  ██╗ ███████╗ ███████╗ ██╗   ██╗  ██████╗  ██╗
 * ██║ ██╔╝ ██╔════╝ ██╔════╝ ██║   ██║ ██╔═══██╗ ██║
 * █████╔╝  █████╗   █████╗   ██║   ██║ ██║   ██║ ██║
 * ██╔═██╗  ██╔══╝   ██╔══╝   ╚██╗ ██╔╝ ██║   ██║ ██║
 * ██║  ██╗ ███████╗ ███████╗  ╚████╔╝  ╚██████╔╝ ███████╗
 * ╚═╝  ╚═╝ ╚══════╝ ╚══════╝   ╚═══╝    ╚═════╝  ╚══════╝
 * </pre>
 * <p>
 * KEEp eVOLution!
 * <p>
 *
 * @author fq@keevol.cn
 * @since 2017.5.12
 * <p>
 * Copyright 2017 © 杭州福强科技有限公司版权所有 (<a href="https://www.keevol.cn">keevol.cn</a>)
 */

import java.util.List;
import java.util.stream.Collectors;


public class AggregateException extends Exception {
    private final List<Throwable> causes;

    public AggregateException(String message, List<Throwable> causes) {
        super(message);
        this.causes = List.copyOf(causes); // Make it immutable
    }

    public List<Throwable> getCauses() {
        return causes;
    }

    @Override
    public String getMessage() {
        String causesMessage = causes.stream()
                .map(t -> "\t- " + t.getMessage())
                .collect(Collectors.joining("\n"));
        return super.getMessage() + "\nUnderlying causes:\n" + causesMessage;
    }
}
