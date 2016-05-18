package org.springframework.boot.autoconfigure.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.GenericConnectionStatusView;
import org.springframework.social.weibo.api.Weibo;
import org.springframework.social.weibo.connect.WeiboConnectionFactory;

@Configuration
@ConditionalOnClass({SocialConfigurerAdapter.class, WeiboConnectionFactory.class})
@ConditionalOnProperty(prefix = "spring.social.weibo", name = "app-id")
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class WeiboAutoConfiguration {

    @Configuration
    @EnableSocial
    @EnableConfigurationProperties(WeiboProperties.class)
    @ConditionalOnWebApplication
    protected static class WeiboConfigurerAdapter extends SocialAutoConfigurerAdapter {

        @Autowired
        private WeiboProperties properties;

        @Bean
        @ConditionalOnMissingBean(Weibo.class)
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public Weibo Weibo(ConnectionRepository repository) {
            Connection<Weibo> connection = repository
                    .findPrimaryConnection(Weibo.class);
            return connection != null ? connection.getApi() : null;
        }

        @Bean(name = {"connect/weiboConnect", "connect/weiboConnected"})
        @ConditionalOnProperty(prefix = "spring.social", name = "auto-connection-views")
        public GenericConnectionStatusView WeiboConnectView() {
            return new GenericConnectionStatusView("weibo", "weibo");
        }

        @Override
        protected ConnectionFactory<?> createConnectionFactory() {
            return new WeiboConnectionFactory(this.properties.getAppId(),
                    this.properties.getAppSecret());
        }

    }

}
