package org.silicon.slang;

import org.silicon.backend.BackendType;
import org.silicon.device.ComputeContext;
import org.silicon.kernel.ComputeModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SlangCompiler {

    private final ComputeContext context;
    private final BackendType backendType;

    public SlangCompiler(ComputeContext context, BackendType backendType) {
        this.context = context;
        this.backendType = backendType;
    }

    public ComputeModule compile(Path path) {
        String target = switch (backendType) {
            case CUDA -> "ptx";
            case METAL -> "metal";
            case OPENCL -> throw new IllegalStateException("OpenCL is not yet supported"); // TODO: spirv
        };

        String fileName = path.getFileName().toString();
        String out = fileName.replaceAll("\\.", "_") + "." + target;

        Path cwd = Paths.get("").toAbsolutePath().normalize();
        Path source = path.isAbsolute()
            ? path.normalize()
            : cwd.resolve(path).normalize();

        Path workDir = source.getParent();
        Path outPath = workDir.resolve(out);

        try {
            ProcessBuilder builder = new ProcessBuilder(
                "slangc", fileName,
                "-target", target,
                "-o", out
            );

            builder.directory(workDir.toFile());
            builder.redirectErrorStream(true);

            Process process = builder.start();

            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("[slangc] " + line); // debugging, TODO: improve debugging and error logging
                }
            }

            int exit = process.waitFor();
            if (exit != 0) {
                throw new RuntimeException("Slang compilation failed with exit code " + exit);
            }

            return context.loadModule(outPath);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
    public ComputeModule compileFromResource(String resourcePath) {
        ClassLoader cl = SlangCompiler.class.getClassLoader();
        
        try (InputStream in = cl.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            
            Path tempDir = Files.createTempDirectory("silicon-slang-");
            String fileName = Path.of(resourcePath).getFileName().toString();
            Path tempSource = tempDir.resolve(fileName);
            
            Files.copy(in, tempSource, StandardCopyOption.REPLACE_EXISTING);
            
            return compile(tempSource);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to compile resource: " + resourcePath, e);
        }
    }
}
