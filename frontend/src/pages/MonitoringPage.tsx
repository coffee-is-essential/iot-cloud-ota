import { MainTile } from "../widgets/layout/ui/MainTile";
import { TitleTile } from "../widgets/layout/ui/TitleTile";

export const MonitoringPage = () => {
  return (
    <div className="flex flex-col">
      <div className="mb-8">
        <TitleTile title="모니터링" description="기기 상태 모니터링" />
      </div>
      <div>
        <MainTile title="모니터링">
          <div className="flex flex-col space-y-4">
            <div className="w-full h-[30em]">
              <iframe
                src="http://grafana-alb-1148491999.ap-northeast-2.elb.amazonaws.com/d-solo/b52031e9-7fab-4ef1-9895-47790008aa28/iot-cloud-ota?orgId=1&timezone=browser&theme=light&panelId=1&__feature.dashboardSceneSolo=true"
                width="100%"
                height="100%"
              ></iframe>
            </div>
            <div className="w-full h-[30em]">
              <iframe
                src="http://grafana-alb-1148491999.ap-northeast-2.elb.amazonaws.com/d-solo/b52031e9-7fab-4ef1-9895-47790008aa28/iot-cloud-ota?orgId=1&timezone=browser&theme=light&panelId=2&__feature.dashboardSceneSolo=true"
                width="100%"
                height="100%"
              ></iframe>
            </div>
          </div>
        </MainTile>
      </div>
    </div>
  );
};
