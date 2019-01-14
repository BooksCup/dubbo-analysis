package com.bc.soa.spi.jdk.serviceloader.impl;

import com.bc.soa.spi.jdk.serviceloader.Command;

/**
 * 开启命令
 *
 * @author zhou
 */
public class StartupCommand implements Command {

    @Override
    public void execute() {
        System.out.println("startup...");
    }
}
