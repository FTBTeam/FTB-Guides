package dev.ftb.mods.ftbguides.integration;

import dev.ftb.mods.ftbguides.client.FTBGuidesClient;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.quest.Quest;
import net.minecraft.network.chat.Component;

public class FTBQuestsIntegration {
    public static void openQuest(String questId) {
        if (ClientQuestFile.exists()) {
            ClientQuestFile.parseHexId(questId).ifPresentOrElse(
                    id -> {
                        Quest quest = ClientQuestFile.INSTANCE.getQuest(id);
                        if (quest != null) {
                            ClientQuestFile.openGui(quest, true);
                        } else {
                            showError(questId);
                        }
                    },
                    () -> showError(questId)
            );
        }
    }

    private static void showError(String questId) {
        FTBGuidesClient.displayError(Component.translatable("ftbguides.message.invalid_quest_id", questId));
    }
}
