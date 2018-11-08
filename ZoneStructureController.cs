﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using WorkFlowCore.Models;


namespace WorkFlowCore.Controllers
{
    public class ZoneStructureController : ApiController
    {
        public ApplicationDbContext _context;

        public ZoneStructureController()
        {
            _context = new ApplicationDbContext("ZenithConnectionString");
        }

        public IHttpActionResult GetZones()
        {
            var Zones = _context.vw_manpowergrouping.ToList();

            if (Zones == null)
            {
                return NotFound();
            }

            return Ok(Zones);
        }

        [Route("api/GetZones/{analysis_id}/{org_id}")]
        public IHttpActionResult GetZonesByADOrgID(int? analysis_id, int org_Id)
        {
            var Zones = _context.vw_manpowergrouping.Where(c => c.org_id == org_Id && c.analysis_id == analysis_id).ToList();

            if (Zones == null)
            {
                return NotFound();
            }

            return Ok(Zones);
        }

        [Route("api/GetZonesByOrgID/{org_id}")]
        public IHttpActionResult GetZonesByOrgID(int org_Id)
        {
            var Zones = _context.vw_manpowergrouping.Where(c => c.org_id == org_Id).ToList();

            if (Zones == null)
            {
                return NotFound();
            }

            return Ok(Zones);
        }


        [Route("api/GetZonesByZoneID/{zone_Id}")]
        public IHttpActionResult GetZonesByZoneID(int zone_Id)
        {
            var Zones = _context.vw_manpowergrouping.Where(c => c.zone_id == zone_Id).ToList();

            if (Zones == null)
            {
                return NotFound();
            }

            return Ok(Zones);
        }


        [Route("api/GetZonesByBranchCode/{branch_code}")]
        public IHttpActionResult GetZonesByBranchCode(string branch_code)
        {
            var Zones = _context.vw_manpowergrouping.Where(c => c.branch_code == branch_code).ToList();

            if (Zones == null)
            {
                return NotFound();
            }

            return Ok(Zones);
        }

    }
}
