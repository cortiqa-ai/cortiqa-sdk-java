package co.cortiqa.sdk.streaming;

import co.cortiqa.sdk.models.ChatCompletionChunk;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Iterable and streamable SSE response from Cortiqa.
 */
public class StreamResponse implements Iterable<ChatCompletionChunk>, AutoCloseable {
    private final BufferedReader reader;
    private final ObjectMapper mapper;
    private ChatCompletionChunk nextChunk = null;
    private boolean done = false;

    public StreamResponse(InputStream inputStream, ObjectMapper mapper) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.mapper = mapper;
    }

    @Override
    public Iterator<ChatCompletionChunk> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                if (done) return false;
                if (nextChunk != null) return true;

                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;

                        if (line.startsWith("data: ")) {
                            String data = line.substring(6).trim();
                            if ("[DONE]".equals(data)) {
                                done = true;
                                return false;
                            }
                            try {
                                nextChunk = mapper.readValue(data, ChatCompletionChunk.class);
                                return true;
                            } catch (Exception e) {
                                // Skip unparseable lines
                            }
                        }
                    }
                    done = true;
                    return false;
                } catch (IOException e) {
                    done = true;
                    return false;
                }
            }

            @Override
            public ChatCompletionChunk next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                ChatCompletionChunk chunk = nextChunk;
                nextChunk = null;
                return chunk;
            }
        };
    }

    /**
     * Java 8+ Stream of ChatCompletionChunks.
     */
    public Stream<ChatCompletionChunk> stream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED | Spliterator.NONNULL),
                false
        );
    }

    /**
     * Java 8+ Stream of raw text tokens directly.
     */
    public Stream<String> textStream() {
        return stream()
                .filter(chunk -> chunk.getChoices() != null && !chunk.getChoices().isEmpty())
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())
                .filter(content -> content != null && !content.isEmpty());
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
