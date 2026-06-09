@RestController
@RequestMapping("/api/auditor")
public class AuditorController {

    @Autowired
    private ActivityService activityService;

    // 获取待审批列表
    @GetMapping("/audit-list")
    public ResultVO<List<Activity>> getAuditList(@RequestParam String orgId) {
        return ResultVO.success(activityService.getToAuditList(orgId));
    }

    // 获取待结算列表
    @GetMapping("/settle-list")
    public ResultVO<List<Activity>> getSettleList(@RequestParam String orgId) {
        return ResultVO.success(activityService.getToSettleList(orgId));
    }

    // 审批接口
    @PostMapping("/approve")
    public ResultVO<Void> approve(@RequestParam String activityId, @RequestParam String currentStatus) {
        String nextStatus;
        if ("等待初审".equals(currentStatus)) {
            nextStatus = "待终审";
        } else if ("待终审".equals(currentStatus)) {
            nextStatus = "待报名";
        } else {
            return ResultVO.error("当前状态无法审批");
        }
        activityService.auditActivity(activityId, nextStatus);
        return ResultVO.success();
    }

    // 驳回接口
    @PostMapping("/reject")
    public ResultVO<Void> reject(@RequestParam String activityId) {
        activityService.auditActivity(activityId, "被驳回");
        return ResultVO.success();
    }

    // 结算完结接口
    @PostMapping("/settle")
    public ResultVO<Void> settle(@RequestParam String activityId) {
        try {
            activityService.settleActivity(activityId);
            return ResultVO.success();
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }
}