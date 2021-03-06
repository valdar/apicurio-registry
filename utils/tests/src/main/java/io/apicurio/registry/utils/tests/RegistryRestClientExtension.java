package io.apicurio.registry.utils.tests;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import io.apicurio.registry.client.RegistryRestClient;
import io.apicurio.registry.client.RegistryRestClientFactory;

/**
 * @author famartin
 */
public class RegistryRestClientExtension implements ParameterResolver {
    
    private static RegistryRestClient CLIENT;
    
    private static final RegistryRestClient getRestClient() {
        if (CLIENT == null) {
            CLIENT = RegistryRestClientFactory.create(TestUtils.getRegistryApiUrl());
        }
        return CLIENT;
    }
    
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Type type = parameterContext.getParameter().getParameterizedType();
        if (type instanceof Class) {
            if (type == RegistryRestClient.class) {
                return true;
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type rawType = pt.getRawType();
            if (rawType == RegistryRestClient.class) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL);
        return store.getOrComputeIfAbsent("registry_rest_client", k -> {
            return getRestClient();
        });
    }

}
