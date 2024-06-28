package dev.ftb.mods.ftbguides.docs;

import net.minecraft.resources.ResourceLocation;
import org.commonmark.internal.util.Parsing;
import org.commonmark.node.Block;
import org.commonmark.parser.block.*;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeNodeParser extends AbstractBlockParser {
    private static final Pattern PAT = Pattern.compile("!recipe\\((\\S+)\\)");

    private final ResourceLocation recipeId;

    public RecipeNodeParser(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public Block getBlock() {
        return new RecipeNode(recipeId);
    }

    @Override
    public BlockContinue tryContinue(ParserState parserState) {
        return BlockContinue.none();
    }

    private static Optional<ResourceLocation> findRecipeId(ParserState state, int index) {
        CharSequence line = state.getLine().getContent();
        if (state.getIndent() < Parsing.CODE_BLOCK_INDENT && index < line.length()) {
            String s = line.toString().substring(index);
            Matcher m = PAT.matcher(s);
            if (m.matches() && ResourceLocation.tryParse(m.group(1)) != null) {
                return Optional.of(ResourceLocation.parse(m.group(1)));
            }
        }
        return Optional.empty();
    }

    public enum Factory implements BlockParserFactory {
        INSTANCE;

        @Override
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
            int nextNonSpace = state.getNextNonSpaceIndex();
            return findRecipeId(state, nextNonSpace)
                    .map(id -> BlockStart.of(new RecipeNodeParser(id)))
                    .orElse(BlockStart.none());
        }

    }
}
