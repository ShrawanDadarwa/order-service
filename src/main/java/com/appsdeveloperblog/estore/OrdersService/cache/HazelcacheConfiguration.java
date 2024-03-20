package com.appsdeveloperblog.estore.OrdersService.cache;


import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcacheConfiguration {



    @Bean
    public HazelcastInstance hazelCastConfig(){
        /*return new Config()
                .setInstanceName("hazelcast-instance")
                .addMapConfig(
                        new MapConfig()
                                .setName("orders").setMaxIdleSeconds(1)
                                .setEvictionConfig(new EvictionConfig())
                                .setTimeToLiveSeconds(20));*/


        Config config = new Config();
        config.getNetworkConfig().setPort( 5900 )
                .setPortAutoIncrement( false );

        MapConfig mapConfig = new MapConfig();
        mapConfig.setName( "testMap" )
                .setBackupCount( 2 )
                .setTimeToLiveSeconds( 300 );

       return Hazelcast.newHazelcastInstance(config);
    }
}
