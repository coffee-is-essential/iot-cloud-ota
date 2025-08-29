package com.coffee_is_essential.iot_cloud_ota.domain;

import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ProgressCount {
    private long totalCount;
    private long successCount;
    private long inProgressCount;
    private long failedCount;

    public static ProgressCount from(List<DeploymentStatusCount> countList) {
        long total = 0, success = 0, inProgress = 0, failed = 0;

        for (DeploymentStatusCount statusCount : countList) {
            if (statusCount.deploymentStatus().equals(DeploymentStatus.IN_PROGRESS.name())) {
                inProgress += statusCount.count();
            } else if (statusCount.deploymentStatus().equals(DeploymentStatus.TIMEOUT.name())) {
                failed += statusCount.count();
            } else if (statusCount.deploymentStatus().equals(DeploymentStatus.SUCCESS.name())) {
                success += statusCount.count();
            } else if (statusCount.deploymentStatus().equals(DeploymentStatus.FAILED.name())) {
                failed += statusCount.count();
            }

            total += statusCount.count();
        }

        return new ProgressCount(total, success, inProgress, failed);
    }
}
