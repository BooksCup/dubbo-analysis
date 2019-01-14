package com.bc.soa.spi.jdk.serviceloader.impl;

import com.bc.soa.spi.jdk.serviceloader.Command;

/**
 * 关闭命令
 *
 * @author zhou
 */
public class ShutdownCommand implements Command {
    @Override
    public void execute() {
        System.out.println("shutdown...");
    }
}
