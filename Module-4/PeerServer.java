import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.file.*;
// import java.io.IOException;
import java.util.stream.Collectors;

public class PeerServer {

    static final int PORT = 10000;
    static final Path SHARED = Paths.get("shared");
    static final Path WEB = Paths.get("web");

    public static void main(String[] args) throws Exception {

        Files.createDirectories(SHARED);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        /* ================= SERVE UI ================= */
        server.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            Path file = WEB.resolve(path.substring(1));

            if (!Files.exists(file)) {
                ex.sendResponseHeaders(404, -1);
                return;
            }

            ex.sendResponseHeaders(200, Files.size(file));
            Files.copy(file, ex.getResponseBody());
            ex.close();
        });

        /* ================= LIST FILES ================= */
        server.createContext("/list", ex -> {
            String response = Files.list(SHARED)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.joining(","));

            byte[] data = response.getBytes();
            ex.sendResponseHeaders(200, data.length);
            ex.getResponseBody().write(data);
            ex.close();
        });

        /* ================= UPLOAD ================= */
        server.createContext("/upload", ex -> {
            if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
                ex.sendResponseHeaders(405, -1);
                return;
            }

            String name = ex.getRequestHeaders().getFirst("X-Filename");
            if (name == null || name.isEmpty()) {
                ex.sendResponseHeaders(400, -1);
                return;
            }

            Files.copy(
                    ex.getRequestBody(),
                    SHARED.resolve(name),
                    StandardCopyOption.REPLACE_EXISTING
            );

            ex.sendResponseHeaders(200, 0);
            ex.close();
        });

        /* ================= DOWNLOAD ================= */
        server.createContext("/download", ex -> {
            String query = ex.getRequestURI().getQuery();
            if (query == null || !query.startsWith("file=")) {
                ex.sendResponseHeaders(400, -1);
                return;
            }

            Path file = SHARED.resolve(query.substring(5));
            if (!Files.exists(file)) {
                ex.sendResponseHeaders(404, -1);
                return;
            }

            ex.sendResponseHeaders(200, Files.size(file));
            Files.copy(file, ex.getResponseBody());
            ex.close();
        });

        server.start();
        System.out.println("Peer running at http://localhost:" + PORT);
    }
}
