package com.bc.soa.spi.jdk;

import com.bc.soa.spi.jdk.serviceloader.Command;

import java.util.ServiceLoader;

/**
 * 主函数
 *
 * @author zhou
 */
public class SpiMain {
    public static void main(String[] args) {
        ServiceLoader<Command> loader = ServiceLoader.load(Command.class);
        System.out.println(loader);

        for (Command command : loader) {
            command.execute();
        }
    }
}
