package pl.fileserver;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import pl.fileserver.consumer.ServiceConsumer;
import pl.fileserver.util.BigFilesManager;
import reactor.core.Environment;
import reactor.io.encoding.StandardCodecs;
import reactor.net.netty.tcp.NettyTcpServer;
import reactor.net.tcp.TcpServer;
import reactor.net.tcp.spec.TcpServerSpec;
import reactor.spring.context.config.EnableReactor;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableReactor
public class FileServerApp {

    @Bean
    @Autowired
    //TODO ServiceConsumer as singleton
    public TcpServer<String, String> serviceApi(Environment env, Integer tcpPort, BigFilesManager bigFilesManager) {
        TcpServer<String, String> server = new TcpServerSpec<String, String>(NettyTcpServer.class)
                .env(env)
                .listen(tcpPort)
                .codec(StandardCodecs.LINE_FEED_CODEC)
                .consume(conn -> conn.in().consume(new ServiceConsumer(bigFilesManager, conn, closeLatch())))
                .get();

        server.start();
        return server;
    }

    @Bean
    public CountDownLatch closeLatch() {
        return new CountDownLatch(1);
    }

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = SpringApplication.run(FileServerApp.class, args);

        CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
        closeLatch.await();
    }

}
