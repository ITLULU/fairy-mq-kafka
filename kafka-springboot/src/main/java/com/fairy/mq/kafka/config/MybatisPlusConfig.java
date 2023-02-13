package com.fairy.mq.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * mybatis配置文件
 *
 * @author 鹿少年
 * @version 1.0
 * @project fairy-repository
 * @createTime 2022/3/20 23:22
 */
@EnableTransactionManagement
@Configuration
//@MapperScan(basePackages = {"com.fairy.kafka.mapper"})
public class MybatisPlusConfig {


    @Bean("platformTransactionManager")
    @Primary
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        return transactionManager;
    }


}
