﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using WorkFlowCore.Models;

namespace WorkFlowCore.Controllers
{
    public class BranchStructureController : ApiController
    {
        public ApplicationDbContext _context;

        public BranchStructureController()
        {
            _context = new ApplicationDbContext("AppraisalDbConnectionString");
        }

        [Route("api/GetBranches")]
        public IHttpActionResult GetBranches()
        {
            var rStructure = _context.zib_workflow_dept_structure.ToList();

            return Ok(rStructure);
            
        }

        //api/ReportingStructure/1
        [Route("api/GetBranchesByAdOrgID/{ad_org_id:int}")]
        public IHttpActionResult GetBranchesByAD_Org_ID(int ad_org_id)
        {

            var rStructure = _context.zib_workflow_dept_structure.Where(c => c.ad_org_id == ad_org_id).ToList();

            if (rStructure == null)
            {
                return NotFound();
            }

            return Ok(rStructure);
        }

        //api/ReportingStructure/1
        [Route("api/GetBranchesByOrgID/{org_id}/{ad_org_id}")]
        public IHttpActionResult GetBranchesByOrg_ID(int org_id , int ad_org_id)
        {
            var rStructure = _context.zib_workflow_dept_structure.Where(c => c.ad_org_id == ad_org_id && c.supergroupcode == org_id).ToList();

            if (rStructure == null)
            {
                return NotFound();
            }

            return Ok(rStructure);
        }

        //api/ReportingStructure/1/1/054
        [Route("api/GetBranchesByZoneID/{ad_org_id}/{org_id}/{zone_id}")]

        public IHttpActionResult GetBranchesByZoneID(int ad_org_id, int org_id, string zone_id)
        {
            var rStructure = _context.zib_workflow_dept_structure.Where(c => c.ad_org_id == ad_org_id && c.supergroupcode == org_id && c.groupcode == zone_id).ToList();

            if (rStructure == null)
            {
                return NotFound();
            }


            return Ok(rStructure);
        }
       

        //api/ReportingStructure/1/1/054
        [Route("api/GetBranchByBranchCode/{ad_org_id}/{org_id}/{branch_code}")]

        public IHttpActionResult GetBranchByBranchCode(int ad_org_id, int org_id, string branch_code)
        {
            var rStructure = _context.zib_workflow_dept_structure.Where(c => c.ad_org_id == ad_org_id && c.supergroupcode == org_id && c.deptcode == branch_code).ToList();

            if (rStructure == null)
            {
                return NotFound();
            }


            return Ok(rStructure);
        }
    }
}
