package com.github.mcmodderanchor.simplebedrockmodel.v2.resource;

import com.github.mcmodderanchor.simplebedrockmodel.SimpleBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterBedrockAnimationEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterBedrockAnimationReloadListenerEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterBedrockModelEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterBedrockModelReloadListenerEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterV2BedrockResourcesEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.particle.resource.ParticleDefinitionLoader;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockAnimationResources;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;

public class ReloadListenersRegister {
    @EventBusSubscriber(modid = SimpleBedrockModel.MOD_ID, value = Dist.CLIENT)
    public static class BedrockModelClientRegister {
        @SubscribeEvent
        public static void onRegisterReloadListener(AddClientReloadListenersEvent event) {
            RegisterBedrockModelEvent event1 = new RegisterBedrockModelEvent(Dist.CLIENT);
            ModLoader.postEvent(event1);
            RegisterBedrockModelReloadListenerEvent event2 = new RegisterBedrockModelReloadListenerEvent();
            ModLoader.postEvent(event2);
            BedrockModelResourceSet.INSTANCE = new BedrockModelResourceSet(event1.getModelRegistry(), event2.getListeners());


            RegisterBedrockAnimationEvent event3 = new RegisterBedrockAnimationEvent(Dist.CLIENT);
            ModLoader.postEvent(event3);
            RegisterBedrockAnimationReloadListenerEvent event4 = new RegisterBedrockAnimationReloadListenerEvent();
            ModLoader.postEvent(event4);
            BedrockAnimationResourceSet.INSTANCE = new BedrockAnimationResourceSet(event3.getAnimationRegistry(), event4.getListeners());


            RegisterV2BedrockResourcesEvent event5 = new RegisterV2BedrockResourcesEvent(Dist.CLIENT);
            ModLoader.postEvent(event5);
            BedrockAnimationResources.INSTANCE = new BedrockAnimationResources(event5.getAnimationRegistry());
            BedrockModelResources.INSTANCE = new BedrockModelResources(event5.getModelRegistry(), event5.getReloadListeners());


            event.addListener(SimpleBedrockModel.modLoc("legacy_models"), BedrockModelResourceSet.INSTANCE);
            event.addListener(SimpleBedrockModel.modLoc("legacy_animations"), BedrockAnimationResourceSet.INSTANCE);
            event.addListener(SimpleBedrockModel.modLoc("animations"), BedrockAnimationResources.INSTANCE);
            event.addListener(SimpleBedrockModel.modLoc("models"), BedrockModelResources.INSTANCE);
            event.addListener(SimpleBedrockModel.modLoc("particles"), ParticleDefinitionLoader.getInstance());
        }
    }

    @EventBusSubscriber(modid = SimpleBedrockModel.MOD_ID, value = Dist.DEDICATED_SERVER)
    public static class BedrockModelServerRegister {
        @SubscribeEvent
        public static void onRegisterReloadListener(AddServerReloadListenersEvent event) {
            RegisterBedrockModelEvent event1 = new RegisterBedrockModelEvent(Dist.DEDICATED_SERVER);
            ModLoader.postEvent(event1);
            RegisterBedrockModelReloadListenerEvent event2 = new RegisterBedrockModelReloadListenerEvent();
            ModLoader.postEvent(event2);
            BedrockModelResourceSet.INSTANCE = new BedrockModelResourceSet(event1.getModelRegistry(), event2.getListeners());


            RegisterBedrockAnimationEvent event3 = new RegisterBedrockAnimationEvent(Dist.DEDICATED_SERVER);
            ModLoader.postEvent(event3);
            RegisterBedrockAnimationReloadListenerEvent event4 = new RegisterBedrockAnimationReloadListenerEvent();
            ModLoader.postEvent(event4);
            BedrockAnimationResourceSet.INSTANCE = new BedrockAnimationResourceSet(event3.getAnimationRegistry(), event4.getListeners());


            RegisterV2BedrockResourcesEvent event5 = new RegisterV2BedrockResourcesEvent(Dist.DEDICATED_SERVER);
            ModLoader.postEvent(event5);
            BedrockAnimationResources.INSTANCE = new BedrockAnimationResources(event5.getAnimationRegistry());
            BedrockModelResources.INSTANCE = new BedrockModelResources(event5.getModelRegistry(), event5.getReloadListeners());


            event.addListener(SimpleBedrockModel.modLoc("legacy_models"), BedrockModelResourceSet.INSTANCE);
            event.addListener(SimpleBedrockModel.modLoc("legacy_animations"), BedrockAnimationResourceSet.INSTANCE);
            event.addListener(SimpleBedrockModel.modLoc("animations"), BedrockAnimationResources.INSTANCE);
            event.addListener(SimpleBedrockModel.modLoc("models"), BedrockModelResources.INSTANCE);
        }
    }
}
