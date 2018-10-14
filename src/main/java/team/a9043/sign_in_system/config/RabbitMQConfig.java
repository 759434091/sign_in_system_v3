package team.a9043.sign_in_system.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class RabbitMQConfig {
    @Bean("exchange")
    public DirectExchange defaultExchange(@Value("${rbmq.exchange}") String exchange) {
        return new DirectExchange(exchange);
    }

    @Bean("queue")
    public Queue queue(@Value("${rbmq.queue}") String queue) {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    @DependsOn({"exchange", "queue"})
    public Binding binding(@Value("${rbmq.bindKey}") String bindKey,
                           @Autowired Queue queue,
                           @Autowired DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(bindKey);
    }
}
