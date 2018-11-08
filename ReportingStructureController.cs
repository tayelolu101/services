using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using WorkFlowCore.Models;

namespace WorkFlowCore.Controllers
{
    public class ReportingStructureController : ApiController
    {
        private ApplicationDbContext _context;

        public ReportingStructureController()
        {
            _context = new ApplicationDbContext("AppraisalDbConnectionString");
        }

        //api/ReportingStructure
        public IHttpActionResult GetReportingStructure()
        {
            var rStructure = _context.zib_leave_approvers.ToList();

            return Ok(rStructure);
        }

        //api/ReportingStructure/1
        [Route("api/GetReportingStructureByAdOrgID/{ad_org_id:int}")]
        public IHttpActionResult GetReportingStructureByAD_Org_ID(int ad_org_id)
        {

            var rStructure = _context.zib_leave_approvers.Where(c => c.ad_org_id == ad_org_id).ToList();

            if (rStructure == null)
            {
                return NotFound();
            }

            return Ok(rStructure);
        }

        //api/ReportingStructure/1
        [Route("api/GetReportingStructureByOrgID/{org_id}")]
        public IHttpActionResult GetReportingStructureByOrgID(int org_id)
        {
            var rStructure = _context.zib_leave_approvers.Where(c => c.org_id == org_id).ToList();

            if (rStructure == null)
            {
                return NotFound();
            }

            return Ok(rStructure);
        }

        //api/ReportingStructure/1/1/054
        [Route("api/GetReportingStructureByZone/{ad_org_id}/{org_id}/{zone_id}")]
        
        public IHttpActionResult GetReportingStructureByZone(int ad_org_id, int org_id, string zone_id)
        {
            var rStructure = _context.zib_leave_approvers.Where(c => c.ad_org_id == ad_org_id && c.org_id == org_id && c.groupcode == zone_id).ToList();

            if (rStructure == null)
            {
                return NotFound();
            }

            return Ok(rStructure);
        }

        //api/ReportingStructure/1/1/054
        [Route("api/GetReportingStructureByBranch/{ad_org_id}/{org_id}/{branch_code}")]
        
        public IHttpActionResult GetReportingStructureByBranch(int ad_org_id, int org_id, string branch_code)
        {
            var rStructure = _context.zib_leave_approvers.Where(c => c.ad_org_id == ad_org_id && c.org_id == org_id && c.deptcode == branch_code).ToList();

            if (rStructure == null)
            {
                return NotFound();
            }


            return Ok(rStructure);
        }

        


    }
}
