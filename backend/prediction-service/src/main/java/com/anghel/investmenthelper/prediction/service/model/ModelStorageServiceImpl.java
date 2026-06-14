package com.anghel.investmenthelper.prediction.service.model;

import com.anghel.investmenthelper.prediction.exception.ModelStorageException;
import com.anghel.investmenthelper.prediction.util.property.PredictionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tribuo.Model;
import org.tribuo.classification.Label;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ModelStorageServiceImpl implements ModelStorageService {

    private final PredictionProperties predictionProperties;

    public ModelStorageServiceImpl(PredictionProperties predictionProperties) {
        this.predictionProperties = predictionProperties;
    }

    @Override
    public String saveModel(String ticker, Model<Label> model, Integer version) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }

        String savePath = predictionProperties.getModelStoragePath();
        Path filePath = null;

        try {
            Path directory = Paths.get(savePath);
            Files.createDirectories(directory);

            String fileName = ticker + "-v" + version + ".model";
            filePath = directory.resolve(fileName);

            model.serializeToFile(filePath);

            log.info("Model saved [ticker={}, version={}, path={}]",
                    ticker,
                    version,
                    filePath
            );

            return filePath.toString();

        } catch (IOException e) {
            throw new ModelStorageException("Failed to save model to path " + filePath, e);
        }
    }

    @Override
    public Model<Label> loadModel(String modelPath) {
        try {
            Path path = Paths.get(modelPath);

            @SuppressWarnings("unchecked")
            Model<Label> model = (Model<Label>) Model.deserializeFromFile(path);
            log.debug("Model loaded [path={}]", modelPath);

            return model;

        } catch (IOException e) {
            throw new ModelStorageException("Failed to load model from path " + modelPath, e);
        }
    }

    @Override
    public void deleteModel(String modelPath) {
        try {
            boolean deleted = Files.deleteIfExists(Paths.get(modelPath));

            if (deleted) {
                log.info("Model deleted successfully [path={}]", modelPath);
            } else {
                log.warn("Model file not found for deletion [path={}]", modelPath);
            }
        } catch (IOException e) {
            log.error("Failed to delete model [path={}]", modelPath, e);
        }
    }
}
