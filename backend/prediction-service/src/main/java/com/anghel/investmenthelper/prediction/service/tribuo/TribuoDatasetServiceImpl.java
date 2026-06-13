package com.anghel.investmenthelper.prediction.service.tribuo;

import com.anghel.investmenthelper.prediction.model.internal.TrainingRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tribuo.Example;
import org.tribuo.Feature;
import org.tribuo.MutableDataset;
import org.tribuo.classification.Label;
import org.tribuo.classification.LabelFactory;
import org.tribuo.datasource.ListDataSource;
import org.tribuo.impl.ArrayExample;
import org.tribuo.provenance.SimpleDataSourceProvenance;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TribuoDatasetServiceImpl implements TribuoDatasetService {

    @Override
    public MutableDataset<Label> buildDataset(List<TrainingRow> trainingRows) {
        LabelFactory labelFactory = new LabelFactory();
        List<Example<Label>> examples = new ArrayList<>();

        for (TrainingRow row : trainingRows) {
            Label label = new Label(row.getPredictionLabel().name());

            ArrayExample<Label> example = new ArrayExample<>(label);

            example.add(new Feature("dailyReturn", row.getDailyReturn()));
            example.add(new Feature("movingAverage5", row.getMovingAverage5()));
            example.add(new Feature("movingAverage20", row.getMovingAverage20()));
            example.add(new Feature("volatility5", row.getVolatility5()));
            example.add(new Feature("volumeChange", row.getVolumeChange()));
            examples.add(example);
        }

        MutableDataset<Label> dataset = new MutableDataset<>(new ListDataSource<>(
                examples,
                labelFactory,
                new SimpleDataSourceProvenance("training-data", labelFactory))
        );

        log.info("Tribuo dataset generated [rows={}]", dataset.size());
        return dataset;
    }
}
