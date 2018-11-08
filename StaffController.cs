using AutoMapper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using WorkFlowCore.DTOs;
using WorkFlowCore.Models;

namespace WorkFlowCore.Controllers
{
    public class StaffController : ApiController
    {
        private ApplicationDbContext _context;

        public StaffController()
        {
            _context = new ApplicationDbContext("AppraisalDbConnectionString");
        }

        public IHttpActionResult GetStaffRequests()
        {

            var rStructure = _context.zib_workflow_master.ToList()
                .Select(Mapper.Map<zib_workflow_master , WorkflowMasterDto>);

            return Ok(rStructure);
        }
        
    }
}
